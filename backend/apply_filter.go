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

// perform @meta match
func (a *apply_filter_) IsMetaSubSet(n *CoreNodeItem, c *CoreQuery) (bool, bool) {
	for _, query_tag := range c.Tags {
		has_node_tag := false
		for _, this_node_tag := range n.MetaTags {
			if query_tag == this_node_tag {
				has_node_tag = true
				break
			}
		}
		if !has_node_tag {
			return false, true
		}
		has_node_tag = false
	}
	return false, false
}

// and perform is_subset on tags
func (a *apply_filter_) IsSubSet(n *CoreNodeItem, c *CoreQuery) (bool, bool) {
	for _, query_tag := range c.Tags {
		has_node_tag := false
		for _, this_node_tag := range n.Tags {
			if query_tag == this_node_tag {
				has_node_tag = true
				break
			}
		}
		if !has_node_tag {
			return false, true
		}
		has_node_tag = false
	}
	return false, false
}
