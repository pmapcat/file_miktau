package main

import (
	"errors"
	"fmt"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

type fs_backend_ struct {
}

var fs_backend = fs_backend_{}

// takes node items, and creates project with such & such structure under the root dir
// fails, if root_dir is not empty
func TestBuildProjectFolderOnDataSet(root_dir string, dataset []*CoreNodeItem) error {
	err := os.MkdirAll(root_dir, DEFAULT_PERMISSION)
	if err != nil {
		return err
	}
	for _, v := range dataset {
		pdir := filepath.Join(append([]string{root_dir}, v.Tags...)...)

		err := os.MkdirAll(pdir, DEFAULT_PERMISSION)
		if err != nil {
			return err
		}
		_, err = os.Create(filepath.Join(pdir, GenerateCollisionFreeFileName(pdir, v.Name)))
		if err != nil {
			return err
		}
	}
	return nil
}

func (f *fs_backend_) getTempDirsCreated() []string {
	fslspoint := fs_ls(os.TempDir())
	tmpdirlist := []string{}
	for _, dir := range fslspoint.Directories {
		if strings.HasPrefix(filepath.Base(dir), TEMP_DIR_PREFIX) {
			tmpdirlist = append(tmpdirlist, dir)
		}
	}
	return tmpdirlist
}

func (f *fs_backend_) DropTempDirsCreated() error {
	log.Info("Removing temporary dirs, that were created on previous runs")
	for _, tempdir := range f.getTempDirsCreated() {
		log.WithField("tempdir", tempdir).Info("Removing directory")
		err := os.RemoveAll(tempdir)
		if err != nil {
			return err
		}
	}
	return nil
}

func (f *fs_backend_) SymlinkInTempGivenPathes(fpathes []string) (string, error) {
	tmpdir, err := ioutil.TempDir("", TEMP_DIR_PREFIX)
	if err != nil {
		return "", err
	}

	for _, v := range fpathes {
		v, err = filepath.Abs(v)
		if err != nil {
			return "", err
		}
		err := os.Symlink(v,
			filepath.Join(tmpdir, GenerateCollisionFreeFileName(tmpdir, filepath.Base(v))))
		if err != nil {
			return "", err
		}
	}
	return tmpdir, nil
}

func (f *fs_backend_) OpenAsSymlinksInASingleFolder(fpathes []string) error {
	if len(fpathes) > MAX_ALLOWED_FILES_TO_BE_OPENED_IN_FILE_EXPLORER {
		return errors.New(fmt.Sprintf("Amount of pathes exceeds limits: %v. When allowed: %v", len(fpathes), MAX_ALLOWED_FILES_TO_BE_OPENED_IN_FILE_EXPLORER))
	}
	temp_dir, err := f.SymlinkInTempGivenPathes(fpathes)
	if err != nil {
		return err
	}
	return OpenFile(temp_dir)
}

func (f *fs_backend_) OpenEachInDefaultProgram(fpathes []string) error {
	if len(fpathes) > MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM {
		return errors.New(fmt.Sprintf("Amount of pathes exceeds limits: %v. When allowed: %v", len(fpathes), MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM))
	}
	for _, v := range fpathes {
		OpenFile(v)
	}
	return nil
}
