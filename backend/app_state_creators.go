package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
)

func NewAppStateOnFolderIdentity(fpath string) (AppState, error) {
	return NewAppStateOnFolder(fpath, func(n *AppStateItem) *AppStateItem { return n })
}

func newAppState(core_dir string, list_of_nodes []*AppStateItem, hook_fns []func(*AppStateItem)) AppState {
	res := AppState{
		nodes:                   []*AppStateItem{},
		core_dir:                core_dir,
		hook_fns:                hook_fns,
		sorting_aggregator:      newThesaurusAndSortingAggregator(),
		sorting_meta_aggregator: newMetaThesaurusAndSortingAggregator(),
		_transact_happening:     false,
	}
	res.MutableRebirthWithNewData(list_of_nodes)
	return res
}

// worker, is a backend helper that would read up
func NewAppStateOnFolder(fpath string, worker func(*AppStateItem) *AppStateItem) (AppState, error) {
	log.Info("Reading the folder: ", fpath)
	log.Info("Reading files, line by line")
	log.Info("Populating AppStateItem with Files metadata")
	cni := []*AppStateItem{}
	err := filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			cni = append(cni, worker(newAppStateItemFromFile(fpath, info, path)))
		}
		return nil
	})
	if err != nil {
		return AppState{}, err
	}
	return newAppState(fpath, cni), nil

}
