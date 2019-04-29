package main

import (
	"os"
	"path/filepath"
)

type AppStateSubsription interface {
	OnAfterRun(*AppState)
	OnBeforeRun(*AppState)
	Accumulate(*AppStateItem)
	Aggregate(*AppStateItem)
}

type AppState struct {
	_on_after_create    []func([]*AppStateItem)
	_on_after_update    []func([]*AppStateItem)
	_call_after_init    []AppStateSubsription
	_call_after_update  []AppStateSubsription
	agg_sorting         *ThesaurusAndSortingAggregator
	agg_meta            *MetaThesaurusAndSortingAggregator
	agg_fs              *FileSystemAggregator
	nodes               []*AppStateItem
	core_dir            string
	_transact_happening bool
}

func NewEmptyAppState() *AppState {
	return NewAppState(EMPTY_DATA_PATH, []*AppStateItem{})
}

func NewAppState(core_dir string, list_of_nodes []*AppStateItem) *AppState {
	tasa := newThesaurusAndSortingAggregator()
	mtasa := newMetaThesaurusAndSortingAggregator()
	res := AppState{
		_on_after_create: []func([]*AppStateItem){MultifySingleHook(FileSystemHook)}, // patch_db.BuildRetrieveSaved(core_dir)

		_on_after_update:    []func([]*AppStateItem){MultifySingleHook(FileSystemHook)}, // patch_db.BuildStoreExisting(core_dir)
		nodes:               []*AppStateItem{},
		core_dir:            core_dir,
		_call_after_init:    []AppStateSubsription{tasa, mtasa},
		_call_after_update:  []AppStateSubsription{tasa, mtasa, newFileSystemAggregator()},
		agg_sorting:         tasa,
		agg_meta:            mtasa,
		_transact_happening: false,
	}
	res.MutableCreate(list_of_nodes)
	return &res
}

// worker, is a backend helper that would read up
func NewAppStateOnFolder(fpath string, worker func(*AppStateItem) *AppStateItem) (*AppState, error) {
	cni := []*AppStateItem{}
	err := filepath.Walk(fpath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if info.IsDir() {
			if IsHiddenDir(info.Name()) {
				return filepath.SkipDir
			}
		} else {
			cni = append(cni, worker(newAppStateItemFromFile(fpath, info, path)))
		}
		return nil
	})
	if err != nil {
		return &AppState{}, err
	}
	return NewAppState(fpath, cni), nil
}

func (a *AppState) AddSubscription(worker AppStateSubsription) {
	a._call_after_update = append(a._call_after_update, worker)

}
