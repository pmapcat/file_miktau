package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

func MustIsFSExist(fpath string) bool {
	item, err := IsFSExist(fpath)
	if err != nil {
		log.WithField("err", err).Fatal("some other kind of FS error")
	}
	return item
}

func MustIsFileExist(fpath string) bool {
	item, err := IsFileExist(fpath)
	if err != nil {
		log.WithField("err", err).Fatal("some other kind of FS stat error")
	}
	return item
}

func IsFSExist(fpath string) (bool, error) {
	_, err := os.Stat(fpath)
	if err == nil {
		return true, nil
	}

	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}

// RelativePath("/home/mik/","/home/mik/babl/gubl") ->  "gubl/"
func RelativePath(root string, fpath string) (string, error) {
	return filepath.Rel(root, fpath)
}
func IsFileExist(fpath string) (bool, error) {
	stat, err := os.Stat(fpath)
	if err == nil {
		if !stat.IsDir() {
			return true, nil
		}
		return false, nil
	}

	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
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

// assume, that os.Remove will return error, if the directory is not empty
// MUST CHECK ON ALL PLATFORMS.
// because, I don't know whether it will work as expected, or procede
// removing everything up to the root dir

// it shouldn't return error. Because, even if error happens,
// it is an expected behaviour
// but, nonetheless, after err appears, terminate the control flow

// basically, drop all empty directories.
// in depth first fashion
// so, newer directories are also removed
func SimplifiedCleanUp(fpath string) error {
	result := []string{}
	err := filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		if info.IsDir() {
			result = append(result, path)
		}
		return nil
	})
	if err != nil {
		return err
	}
	// For this to work, following properties must hold:
	//   * all items in result are directories
	//   * remove will not remove non-empty directories
	//   * at first go specific directories. Meaning:
	//    	If these are empty. thus deleted, thus their parent dirs.
	//      (who go next), are also deleted, mimicking depth first traversal

	// reverse iterate, same as range, but in reverse order
	for i := len(result) - 1; i >= 0; i-- {
		LogInfoErr("Removing dirs, **must error** on non empty dirs", os.Remove(result[i]))
	}
	return nil
}

func jp(data ...string) string {
	return filepath.Join(data...)
}

// with removal of stale (empty directories)
// with creating of new fpath
// TESTED
func DoFileMoving(old_fpath, new_fpath string) (string, error) {
	new_dir := filepath.Dir(new_fpath)
	new_name := filepath.Base(new_fpath)
	LogErr("On file moving error: ", os.MkdirAll(new_dir, DEFAULT_PERMISSION))
	abspath := filepath.Join(new_dir, GenerateCollisionFreeFileName(new_dir, new_name))
	log.WithField("abspath", abspath).Debug("With such dir")
	return abspath, os.Rename(old_fpath, abspath)
}

// if such file exists, then do: fname.mp4 -> fname_1.mp4
// TESTED
func GenerateCollisionFreeFileName(fdir string, fname string) string {
	if fname == "" {
		fname = "undefined"
	}
	counter := 1
	file_extension := filepath.Ext(fname)
	file_basename := strings.TrimSuffix(fname, file_extension)
	file_basename_with_numcode := file_basename

	for MustIsFileExist(filepath.Join(fdir, str(file_basename_with_numcode, file_extension))) {
		file_basename_with_numcode = str(file_basename, "_", strconv.Itoa(counter))
		counter += 1
	}
	return str(file_basename_with_numcode, file_extension)
}
