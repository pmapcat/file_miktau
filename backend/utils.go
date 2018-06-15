package main

import (
	log "github.com/sirupsen/logrus"
	"math/rand"
	"os"
	"path/filepath"
	"sort"
	"strconv"
	"strings"
	"time"
)

func LogErr(err_msg string, err error) {
	if err != nil {
		log.WithField("err", err).Error(err_msg)
	}
}

func IsMetaTag(tag string) bool {
	return strings.HasPrefix(tag, "@")
}

// panics if can't convert
// (useful when 100% sure that result returns int, as in tests,for example)
func forTestingStringToInt(item string) int {
	a, err := strconv.Atoi(item)
	if err != nil {
		panic(err)
		return 0
	}
	return a
}

func withExt(ext_name string, ext_list []string, meta_list *[]string, ext string) {
	for _, v := range ext_list {
		if v == ext {
			*meta_list = append(*meta_list, ext_name)
			return
		}
	}
}

func sort_slice(inverse bool, slice interface{}, less func(i, j int) bool) {
	if inverse {
		sort.Slice(slice, func(i, j int) bool {
			return !less(i, j)
		})
		return
	}
	sort.Slice(slice, less)
	return
}

// pages are from 1, to last_page
// each page can be selected, and page 1 is a beginning of the dataset
func PaginatorToSlice(total_amount int, page_size int, page int) (int, int, int) {
	if page_size <= 0 {
		page_size = DEFAULT_PAGE_SIZE
	}
	if page <= 0 {
		page = 1
	}

	if page_size > total_amount {
		return 0, total_amount, 1
	}
	if page_size <= 0 {
		page_size = DEFAULT_PAGE_SIZE
	}

	if total_amount <= 0 {
		return 0, 0, 0
	}

	left_offset := (page - 1) * page_size
	right_offset := (page) * page_size

	if left_offset < 0 {
		left_offset = 0
	}
	if right_offset > total_amount {
		right_offset = total_amount
	}
	total_pages := total_amount / (right_offset - left_offset)

	return left_offset, right_offset, total_pages
}

func timeEval(perfname string, cb func()) {
	start := time.Now()
	cb()
	elapsed := time.Since(start)
	log.Printf("%s took: %s", perfname, elapsed)
}
func randomTime() time.Time {
	return time.Unix(rand.Int63(), rand.Int63())
}
func str(data ...string) string {
	return strings.Join(data, "")
}

func is_dev_environ() bool {
	return os.Getenv("go_environ") != "production"
}
func is_prod_environ() bool {
	return os.Getenv("go_environ") == "production"
}

func split_meta_tags(tags []string) ([]string, []string) {
	a := []string{}
	b := []string{}
	for _, tag := range tags {
		if strings.HasPrefix(tag, "@") {
			a = append(a, tag)
		} else {
			b = append(b, tag)
		}
	}
	return a, b
}

func pivot_on(items []string, pivot_fn func(string) bool) ([]string, []string) {
	a := []string{}
	b := []string{}
	for _, v := range items {
		if pivot_fn(v) {
			b = append(b, v)
		} else {
			a = append(a, v)
		}
	}
	return a, b
}

func is_subset(subset, set []string) bool {
	for _, set_item := range set {
		has_item := false
		for _, subset_item := range subset {
			if set_item == subset_item {
				has_item = true
				break
			}
		}
		if !has_item {
			return false
		}
		has_item = false
	}
	return true
}

// will generate random (lorem ipsum) text based on
// input and output distribution
func markovLorem(input string) func(outlen int) string {
	var_arr := strings.Split(input, " ")
	// it is easier to have rand_nth on a list with dublicates than doing
	// non uniform random over a distribution in hashmap

	// btw, I have no idea whether I should shuffle res
	// after using the algorithm.
	res := map[string][]string{}
	for i := 1; i < len(var_arr); i++ {
		item, ok := res[var_arr[i-1]]
		if !ok {
			res[var_arr[i-1]] = []string{}
		}
		item = append(item, var_arr[i])
		res[var_arr[i-1]] = item
	}
	return func(maxlen int) string {
		if maxlen == 0 {
			return ""
		}
		return_data := []string{var_arr[rand.Intn(len(var_arr))]}
		for i := 0; i < maxlen; i++ {
			seed := return_data[len(return_data)-1]
			if len(res[seed]) == 0 {
				return strings.Join(return_data, " ")
			}
			return_data = append(return_data, res[seed][rand.Intn(len(res[seed]))])
		}
		return strings.Join(return_data, " ")
	}
}

func IsFileExist(fpath string) bool {
	_, err := os.Stat(fpath)
	return !os.IsNotExist(err)
}

func WithDir(fpath string, cb func()) error {
	err := os.MkdirAll(fpath, 0777)
	if err != nil {
		return err
	}
	cb()
	return os.RemoveAll(fpath)
}

type FsLsReturnType struct {
	Self        string
	Files       []string
	Directories []string
	Error       error
}

func fs_ls(fpath string) FsLsReturnType {
	fslsreturntype := FsLsReturnType{Files: []string{}, Directories: []string{}}
	fslsreturntype.Error = filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		// in case of curdir
		if fpath == path {
			fslsreturntype.Self = fpath
			return nil
		}
		if info.IsDir() {
			fslsreturntype.Directories = append(fslsreturntype.Directories, path)
			return filepath.SkipDir
		}
		fslsreturntype.Files = append(fslsreturntype.Files, path)
		return nil
	})
	return fslsreturntype
}

// if such file exists, then do: fname.mp4 -> fname_1.mp4
func GenerateCollisionFreeFileName(fdir string, fname string) string {
	if fname == "" {
		fname = "undefined"
	}
	counter := 1
	file_extension := filepath.Ext(fname)
	file_basename := strings.TrimSuffix(fname, file_extension)
	file_basename_with_numcode := file_basename

	for IsFileExist(filepath.Join(fdir, str(file_basename_with_numcode, file_extension))) {
		file_basename_with_numcode = str(file_basename, "_", strconv.Itoa(counter))
		counter += 1
	}
	return str(file_basename_with_numcode, file_extension)
}
