package main

import (
	"os"
	"sync"
)

type AppState struct {
	sync.RWMutex
	hook_fns            []func(*AppStateItem)
	agg_sorting         ThesaurusAndSortingAggregator
	agg_meta            MetaThesaurusAndSortingAggregator
	nodes               []*AppStateItem
	core_dir            string
	_transact_happening bool
}

type AppStateItem struct {
	Id                      int         `json:"id"`
	Name                    string      `json:"name"`
	FilePath                string      `json:"file-path"`
	FileInfo                os.FileInfo `json:"-"`
	MetaTags                []string    `json:"meta-tags"`
	Tags                    []string    `json:"tags"`
	FileSizeInMb            int         `json:"file-size-in-mb"`
	FileExtensionLowerCased string      `json:"file-extension-lower-cased"`
	Modified                JSONTime    `json:"modified"`
}
