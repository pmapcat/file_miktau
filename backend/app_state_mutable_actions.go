package main

import (
	"os"
)

// will have to happen on every <mutable> action on a database
// shall nest without problem. `_transact` shall bubble up to the highest
// level. Meaning, it won't be called excessivly, only on highest level it should
// be called
func (c *AppState) _transact(worker func()) {
	if c._transact_happening {
		worker()
		return
	}
	c._transact_happening = true
	worker()

	c.agg_sorting.OnBeforeRun()
	c.agg_meta.OnBeforeRun()

	for _, v := range c.nodes {
		c.agg_sorting.Accumulate(v)
		c.agg_meta.Accumulate(v)

	}
	for _, v := range c.nodes {
		c.agg_sorting.Aggregate(v)
		c.agg_meta.Aggregate(v)
	}
	c._transact_happening = false
}

func (n *AppState) MutableDrop() {
	n.core_dir = ""
	n.agg_sorting = newThesaurusAndSortingAggregator()
	n.agg_meta = newMetaThesaurusAndSortingAggregator()
	n.nodes = []*AppStateItem{}
}

func (n *AppState) MutableCreate(nodes []*AppStateItem) []int {
	new_ids := []int{}
	n._transact(func() {
		for _, node := range nodes {
			node.Id = len(n.nodes)
			node = n.MutableApplyHooks(node)
			n.nodes = append(n.nodes, node)
			new_ids = append(new_ids, node.Id)
		}
	})
	return new_ids
}

func (n *AppState) MutableApplyHooks(item *AppStateItem) *AppStateItem {
	for _, v := range n.hook_fns {
		v(item)
	}
	return item
}

func (n *AppState) MutableUpdate(query Query, cb func(*AppStateItem) *AppStateItem) {
	n._transact(func() {
		for k, v := range n.nodes {
			if v.ApplyFilter(&query) {
				n.nodes[k] = n.MutableApplyHooks(cb(v))
			}
		}
	})
}

// filepath: TODO: TEST
func (n *AppState) MutablePushNewFiles(file_paths []string) ([]int, error) {
	// resolve files by comparing them to these already within the root FS
	resolved_items, unresolved_items, err := n.SideEffectResolveIfPossibleWithinFileSystem(file_paths)
	if err != nil {
		return []int{}, err
	}
	// take unresolved items, and extract strings from them
	unresolved_items_strings := []string{}
	for _, v := range unresolved_items {
		unresolved_items_strings = append(unresolved_items_strings, v.CameWithPath)
	}

	// symlink unresolved into <root> directory
	unresolved_as_new_pathes, err := fs_backend.SymlinkInRootGivenForeignPathes(n.core_dir, unresolved_items_strings)
	new_core_node_items := []*AppStateItem{}
	for _, item := range unresolved_as_new_pathes {
		finfo, err := os.Stat(item)
		if err != nil {
			LogErr("This is the error", err)
			continue
		}
		new_core_node_items = append(new_core_node_items, newAppStateItemFromFile(n.core_dir, finfo, item))
	}
	// create new records for these newly symlinked items into root directory
	result_ids := n.MutableCreate(new_core_node_items)

	// save ids from previous operation
	for _, v := range resolved_items {
		result_ids = append(result_ids, v.Node.Id)
	}
	// and return it back to the user
	return result_ids, nil
}

func (n *AppState) MutableAddRemoveTagsToSelection(query Query, tags_to_add, tags_to_remove []string) int {
	records_affected := 0
	n.MutableUpdate(query, func(node *AppStateItem) *AppStateItem {
		node.RemoveTags(tags_to_remove)
		node.AddTags(tags_to_add)
		records_affected += 1
		return node
	})
	return records_affected
}

func (n *AppState) MutableRebirthWithNewData(nodes []*AppStateItem) {
	n.MutableDrop()
	n.MutableCreate(nodes)
}
