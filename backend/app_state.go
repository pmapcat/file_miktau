package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
)

type AppState struct {
	hook_fns            []func(*AppStateItem)
	agg_sorting         *ThesaurusAndSortingAggregator
	agg_meta            *MetaThesaurusAndSortingAggregator
	nodes               []*AppStateItem
	core_dir            string
	_transact_happening bool
}

func NewAppStateFromDachaDataSet(repeat int) *AppState {
	nodes_list := []*AppStateItem{}
	for i := 0; i <= repeat; i++ {
		nodes_list = append(nodes_list, buildDachaDataset()...)
	}
	return NewAppState(DEMO_DACHA_PATH, nodes_list)
}

func NewAppStateFromDemoDataSet(repeat int) *AppState {
	nodes_list := []*AppStateItem{}
	for i := 0; i <= repeat; i++ {
		nodes_list = append(nodes_list, buildDemoDataset()...)
	}
	return NewAppState(DEMO_DATA_PATH, buildDemoDataset())
}

func NewEmptyAppState() *AppState {
	return NewAppState(EMPTY_DATA_PATH, []*AppStateItem{})
}

func NewAppState(core_dir string, list_of_nodes []*AppStateItem) *AppState {
	res := AppState{
		nodes:               []*AppStateItem{},
		core_dir:            core_dir,
		hook_fns:            HOOKS_LIST,
		agg_sorting:         newThesaurusAndSortingAggregator(),
		agg_meta:            newMetaThesaurusAndSortingAggregator(),
		_transact_happening: false,
	}
	res.MutableRebirthWithNewData(list_of_nodes)
	return &res
}

// worker, is a backend helper that would read up
func NewAppStateOnFolder(fpath string, worker func(*AppStateItem) *AppStateItem) (*AppState, error) {
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
		return &AppState{}, err
	}
	return NewAppState(fpath, cni), nil

}
