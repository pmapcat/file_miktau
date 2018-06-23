package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
)

type AppStateSubsription interface {
	OnBeforeRun()
	Accumulate(*AppStateItem)
	Aggregate(*AppStateItem)
}

type AppState struct {
	_on_after_create              []func([]*AppStateItem)
	_on_after_update              []func([]*AppStateItem)
	_rebuild_after_mutable_action []AppStateSubsription
	agg_sorting                   *ThesaurusAndSortingAggregator
	agg_meta                      *MetaThesaurusAndSortingAggregator
	nodes                         []*AppStateItem
	core_dir                      string
	_transact_happening           bool
}

func NewAppStateFromDachaDataSet(repeat int) *AppState {
	nodes_list := []*AppStateItem{}
	for i := 0; i < repeat; i++ {
		nodes_list = append(nodes_list, buildDachaDataset()...)
	}
	return NewAppState(DEMO_DACHA_PATH, nodes_list)
}

func NewAppStateFromDemoDataSet(repeat int) *AppState {
	nodes_list := []*AppStateItem{}
	for i := 0; i < repeat; i++ {
		nodes_list = append(nodes_list, buildDemoDataset()...)
	}
	return NewAppState(DEMO_DATA_PATH, nodes_list)
}

func NewEmptyAppState() *AppState {
	return NewAppState(EMPTY_DATA_PATH, []*AppStateItem{})
}

func NewAppState(core_dir string, list_of_nodes []*AppStateItem) *AppState {
	tasa := newThesaurusAndSortingAggregator()
	mtasa := newMetaThesaurusAndSortingAggregator()
	res := AppState{
		_on_after_create: []func([]*AppStateItem){MultifySingleHook(FileSystemHook)},// patch_db.BuildRetrieveSaved(core_dir)

		_on_after_update: []func([]*AppStateItem){MultifySingleHook(FileSystemHook)},// patch_db.BuildStoreExisting(core_dir)

		nodes:                         []*AppStateItem{},
		core_dir:                      core_dir,
		_rebuild_after_mutable_action: []AppStateSubsription{tasa, mtasa},
		agg_sorting:                   tasa,
		agg_meta:                      mtasa,
		_transact_happening:           false,
	}
	res.MutableCreate(list_of_nodes)
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
