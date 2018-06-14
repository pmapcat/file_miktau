package main

import (
	"errors"
	"fmt"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"os"
	"path/filepath"
)

type fs_backend_ struct {
}

var fs_backend = fs_backend_{}

func (f *fs_backend_) BuildEmptyAppState(fpath string) ([]*CoreNodeItem, error) {
	log.Println("Reading the folder: ", fpath)
	log.Println("Reading files, line by line")
	log.Println("Populating CoreNodeItem with Files metadata")
	return []*CoreNodeItem{}, nil
}

func (f *fs_backend_) BuildAppStateWithNoUserTags(fpath string) ([]*CoreNodeItem, error) {
	log.Println("Reading the folder: ", fpath)
	log.Println("Reading files, line by line")
	log.Println("Populating CoreNodeItem with Files metadata")
	result := buildDemoDataset()
	for k, v := range result {
		v.Tags = []string{}
		result[k] = v
	}
	return result, nil
}

func (f *fs_backend_) BuildAppStateOnAFolder(fpath string) ([]*CoreNodeItem, error) {
	log.Println("Reading the folder: ", fpath)
	log.Println("Reading files, line by line")
	log.Println("Populating CoreNodeItem with Files metadata")
	cni := []*CoreNodeItem{}
	return cni, filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			cni = append(cni, newCoreNodeItemFromFile(fpath, info, path))
		}
		return nil
	})
}

func (f *fs_backend_) SymlinkInTempGivenPathes(fpathes []string) (string, error) {
	tmpdir, err := ioutil.TempDir("", "symlink_")
	if err != nil {
		return "", err
	}

	for _, v := range fpathes {
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
