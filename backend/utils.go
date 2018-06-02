package main

import (
	"log"
	"math/rand"
	"sort"
	"strings"
	"time"
)

func withExt(ext_name string, ext_list []string, meta_list []string, ext string) {
	for _, v := range ext_list {
		if v == ext {
			meta_list = append(meta_list, ext_name)
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
