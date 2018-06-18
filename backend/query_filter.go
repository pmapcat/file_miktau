package main

import (
	"path/filepath"
)

type query_filter_ struct{}

var query_filter = query_filter_{}

// if query contains filepathes list, then it is most likely
// that we must filter by filepaths
func (a *query_filter_) FilterByFilePaths(n *AppStateItem, c *Query) (bool, bool) {
	if len(c.FilePaths) > 0 {
		fname_fpath := filepath.Join(n.FilePath, n.Name)
		for _, fpath := range c.FilePaths {
			if fname_fpath == fpath {
				return true, true
			}
		}
		return false, true
	}
	return false, false
}

// if query contains ids list, then it is likely that we
// must filter by ids
func (a *query_filter_) FilterByIds(n *AppStateItem, c *Query) (bool, bool) {
	if len(c.Ids) > 0 {
		for _, id := range c.Ids {
			if n.Id == id {
				return true, true
			}
		}
		return false, true
	}
	return false, false
}

func (a *query_filter_) IsSubSet(node_tags, query_tags []string) (bool, bool) {
	if len(query_tags) == 0 {
		return false, false
	}
	return is_subset(node_tags, query_tags), true
}
