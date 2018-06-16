package main

import (
	"os"
	fp "path/filepath"
	"strings"
)

// must run after other aggregators are run
// algorithm:
//   * take file under old path
//   * take tags
//   * build new path
//   * if same, skip
//   * if not same, move file into new directory

type FsAggregator struct {
}

// within root directory
// build new path,
func (t *MetaThesaurusAndSortingAggregator) Aggregate(root_dir string, m *CoreNodeItem) {

	new_fpath := fp.Join(root_dir, strings.Join(m.Tags, string(fp.Separator)), m.FileInfo.Name())
	if new_fpath == m.FilePath {
		return
	}
	err := LogErr("MetaThesaurusAndSortingAggregatorError", DoFileMoving(m.FilePath, new_fpath))
	if err != nil {
		return
	}

	// allright, replace file with new file item
	finfo, err := os.Stat(new_fpath)
	err = LogErr("Getting File stat on new files", err)
	if err != nil {
		return
	}
	zob := newCoreNodeItemFromFile(root_dir, finfo, new_fpath)
	zob.Id = m.Id
	zob.MetaTags = m.MetaTags
	*m = *zob

}
