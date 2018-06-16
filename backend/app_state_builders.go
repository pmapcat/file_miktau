package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
)

func NewAppStateOnFolderIdentity(fpath string) ([]*CoreNodeItem, error) {
	return NewAppStateOnFolder(fpath, func(n *CoreNodeItem) *CoreNodeItem { return n })
}

// worker, is a backend helper that would read up
func NewAppStateOnFolder(fpath string, worker func(*CoreNodeItem) *CoreNodeItem) ([]*CoreNodeItem, error) {
	log.Info("Reading the folder: ", fpath)
	log.Info("Reading files, line by line")
	log.Info("Populating CoreNodeItem with Files metadata")
	cni := []*CoreNodeItem{}
	return cni, filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			cni = append(cni, worker(newCoreNodeItemFromFile(fpath, info, path)))
		}
		return nil
	})
}
