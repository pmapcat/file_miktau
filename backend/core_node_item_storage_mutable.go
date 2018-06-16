package main

import (
	// log "github.com/sirupsen/logrus"
	"os"
	// "path/filepath"
	// "strings"
)

// will have to happen on every <mutable> action on a database
// shall nest without problem. `_transact` shall bubble up to the highest
// level. Meaning, it won't be called excessivly, only on highest level it should
// be called
func (c *CoreNodeItemStorage) _transact(worker func()) {
	if c._transact_happening {
		worker()
		return
	}
	c._transact_happening = true
	worker()

	c.sorting_aggregator.OnBeforeRun()
	c.sorting_meta_aggregator.OnBeforeRun()

	for _, v := range c.nodes {
		c.sorting_aggregator.Accumulate(v)
		c.sorting_meta_aggregator.Accumulate(v)
	}
	for _, v := range c.nodes {
		c.sorting_aggregator.Aggregate(v)
		c.sorting_meta_aggregator.Aggregate(v)
	}

	c._transact_happening = false
}
func (n *CoreNodeItemStorage) MutableDrop() {
	n.core_dir = ""
	n.sorting_aggregator = ThesaurusAndSortingAggregator{}
	n.nodes = []*CoreNodeItem{}
}

func (n *CoreNodeItemStorage) __mutableCreate(nodes []*CoreNodeItem) []int {
	new_ids := []int{}
	n._transact(func() {
		for _, node := range nodes {
			node.Id = len(n.nodes)
			CallHooks(node)
			n.nodes = append(n.nodes, node)
			new_ids = append(new_ids, node.Id)
		}
	})
}

func (n *CoreNodeItemStorage) __mutableUpdate(query CoreQuery, cb func(*CoreNodeItem) *CoreNodeItem) {
	n._transact(func() {
		for k, v := range n.nodes {
			if v.ApplyFilter(&query) {
				fo := cb(v)
				CallHooks(fo)
				n.nodes[k] = fo
			}
		}
	})
}

// filepath: TODO: TEST
func (n *CoreNodeItemStorage) MutablePushNewFiles(root string, file_paths []string) ([]int, error) {
	// resolve files by comparing them to these already within the root FS
	resolved_items, unresolved_items, err := n.ResolveIfPossibleWithinTheSystem(file_paths)
	if err != nil {
		return []int{}, err
	}
	// take unresolved items, and extract strings from them
	unresolved_items_strings := []string{}
	for _, v := range unresolved_items {
		unresolved_items_strings = append(unresolved_items_strings, v.CameWithPath)
	}

	// symlink unresolved into <root> directory
	unresolved_as_new_pathes, err := fs_backend.SymlinkInRootGivenForeignPathes(root, unresolved_items_strings)
	new_core_node_items := []*CoreNodeItem{}
	for _, item := range unresolved_as_new_pathes {
		finfo, err := os.Stat(item)
		if err != nil {
			LogErr("This is the error", err)
			continue
		}
		new_core_node_items = append(new_core_node_items, newCoreNodeItemFromFile(root, finfo, item))
	}
	// create new records for these newly symlinked items into root directory
	result_ids := n.__mutableCreate(new_core_node_items)

	// save ids from previous operation
	for _, v := range resolved_items {
		result_ids = append(result_ids, v.Node.Id)
	}
	// and return it back to the user
	return result_ids, nil
}

func (n *CoreNodeItemStorage) MutableAddRemoveTagsToSelection(query CoreQuery, tags_to_add, tags_to_remove []string) int {
	records_affected := 0
	n.__mutableUpdate(query, func(node *CoreNodeItem) *CoreNodeItem {
		node.RemoveTags(tags_to_remove)
		node.AddTags(tags_to_add)
		records_affected += 1
		return node
	})
	return records_affected
}

func (n *CoreNodeItemStorage) MutableRebirthWithNewData(nodes []*CoreNodeItem) {
	n.MutableDrop()
	n.__mutableCreate(nodes)
}
