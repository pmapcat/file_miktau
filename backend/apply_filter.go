package main

import (
	"path/filepath"
)

type apply_filter_ struct{}

var apply_filter = apply_filter_{}

// if query contains filepathes list, then it is most likely
// that we must filter by filepaths
func (a *apply_filter_) FilterByFilePaths(n *CoreNodeItem, c *CoreQuery) (bool, bool) {
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
func (a *apply_filter_) FilterByIds(n *CoreNodeItem, c *CoreQuery) (bool, bool) {
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

// match all tags
func (a *apply_filter_) MatchEmpty(n *CoreNodeItem, c *CoreQuery) (bool, bool) {
	if len(c.Tags) == 0 {
		return true, true
	}
	return false, false
}

func (a *apply_filter_) IsSubSet(node_tags, query_tags []string) (bool, bool) {
	return is_subset(node_tags, query_tags), true
}
