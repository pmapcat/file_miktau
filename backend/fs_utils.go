package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

func IsFSExist(fpath string) bool {
	_, err := os.Stat(fpath)
	return !os.IsNotExist(err)
}

func IsFileExist(fpath string) bool {
	stat, err := os.Stat(fpath)
	does_not_exist := os.IsNotExist(err)
	// does not exist
	if does_not_exist {
		return false
	}
	// is not a file
	if stat.IsDir() {
		return false
	}
	// otherwise, true
	return true
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
	return filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		result = append(result, path)
		if err != nil {
			return err
		}
		if !info.IsDir() {
			return nil
		}

		// if empty, will remove without error
		//   * so, if removed, cannot drill into it, thus: return .SkipDir
		// otherwise, continue drilling
		err = os.Remove(path)
		if err != nil {
			return nil
		}
		return filepath.SkipDir
	})

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

	for IsFileExist(filepath.Join(fdir, str(file_basename_with_numcode, file_extension))) {
		file_basename_with_numcode = str(file_basename, "_", strconv.Itoa(counter))
		counter += 1
	}
	return str(file_basename_with_numcode, file_extension)
}
