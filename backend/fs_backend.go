package main

import (
	"errors"
	"fmt"
	"log"
)

type fs_backend_ struct {
}

var fs_backend = fs_backend_{}

func (f *fs_backend_) OpenAsSymlinksInASingleFolder(fpathes []string) error {
	log.Println("Made symlinks of  ", len(fpathes), " files to /tmp/xyz")
	log.Println("Some of them were renamed into (1) (2) variants")
	log.Println("Now opening /tmp/xyz in file browser")
	return nil
}

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
	return buildDemoDataset(), nil
}

func (f *fs_backend_) OpenEachInDefaultProgram(fpathes []string) error {
	if len(fpathes) > MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM {
		return errors.New(fmt.Sprintf("Amount of pathes exceeds limits: %v. When allowed: %v", len(fpathes), MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM))
	}
	log.Println("Opening files in default program")
	return nil
}
func (f *fs_backend_) OpenEachInFileExplorer(fpathes []string) error {
	if len(fpathes) > MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM {
		return errors.New(fmt.Sprintf("Amount of pathes exceeds limits: %v. When allowed: %v", len(fpathes), MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM))
	}
	log.Println("Opening files in explorer")
	return nil
}
