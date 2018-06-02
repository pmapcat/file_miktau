package main

import (
	"os"
	fp "path/filepath"
	"strings"
)

func newMetaThesaurusAndSortingAggregator() *ThesaurusAndSortingAggregator {
	r := ThesaurusAndSortingAggregator{}
	r.reinit()
	return &r
}
func newThesaurusAndSortingAggregator() *ThesaurusAndSortingAggregator {
	r := ThesaurusAndSortingAggregator{}
	r.reinit()
	return &r
}

func newCoreNodeItemStorage(core_dir string) CoreNodeItemStorage {
	return CoreNodeItemStorage{
		nodes:                   []*CoreNodeItem{},
		core_dir:                core_dir,
		sorting_aggregator:      ThesaurusAndSortingAggregator{},
		sorting_meta_aggregator: MetaThesaurusAndSortingAggregator{},
		_transact_happening:     false,
	}
}

func newCoreNodeItemFromStat(filepath string, stat os.FileInfo) *CoreNodeItem {

	return &CoreNodeItem{
		Id:                      -1,
		Name:                    stat.Name(),
		FilePath:                filepath,
		MetaTags:                []string{},
		Tags:                    []string{},
		FileSizeInMb:            int(stat.Size() / 1048576),
		FileExtensionLowerCased: strings.ToLower(fp.Ext(stat.Name())),
		Modified:                stat.ModTime(),
	}
}

func newCoreQuery() *CoreQuery {
	return &CoreQuery{
		PageSize: DEFAULT_PAGE_SIZE,
		Page:     1,
		Sorted:   "",
		Ids:      []int{},
		Tags:     []string{},
	}
}
