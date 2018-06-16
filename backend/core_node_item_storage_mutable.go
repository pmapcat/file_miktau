package main

import (
	log "github.com/sirupsen/logrus"
	"os"
	"path/filepath"
	"strings"
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

func (n *CoreNodeItemStorage) MutableCreate(nodes []*CoreNodeItem) {
	n._transact(func() {
		for _, node := range nodes {
			node.Id = len(n.nodes)
			CallHooks(node)
			n.nodes = append(n.nodes, node)
		}
	})
}

func (n *CoreNodeItemStorage) MutableUpdate(query CoreQuery, cb func(*CoreNodeItem) *CoreNodeItem) {
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

type ResolverNodeItem struct {
	Node         *CoreNodeItem
	CameWithPath string
	WithResolver os.FileInfo
}

// for each file, check whether it is already in
// This is O(N*K) algorithm, where K is the amount of file_pathes.
// On larger systems, it might take larger time to work with it
func (n *CoreNodeItemStorage) ResolveIfPossibleWithinTheSystem(file_paths []string) ([]*ResolverNodeItem, error) {
	finfos := []*ResolverNodeItem{}
	for _, v := range file_paths {
		stat, err := os.Lstat(v)
		if err != nil {
			log.WithError(err).WithField("path", v).
				Error("Path is not resolvable, skipping")
			continue
		}
		finfos = append(finfos, &ResolverNodeItem{CameWithPath: v, WithResolver: stat})
	}

	if len(finfos) == 0 {
		log.Info("Empty list of data, terminating prematurely, saving CPU cycles")
		return []*ResolverNodeItem{}, nil
	}

	results := []*ResolverNodeItem{}
	for _, node := range n.nodes {
		for point_index, resolver_node := range finfos {
			if resolver_node == nil {
				continue
			}
			if os.SameFile(node.FileInfo, resolver_node.WithResolver) {
				resolver_node.Node = node
				results = append(results, resolver_node)
				finfos[point_index] = nil
			}
			break
		}
	}
	return results, nil
}

// filepath.
func (n *CoreNodeItemStorage) MutablePushNewFiles(file_paths []string) ([]int, error) {
	return []int{}, nil
}

func (n *CoreNodeItemStorage) MutableAddRemoveTagsToSelection(query CoreQuery, tags_to_add, tags_to_remove []string) int {
	records_affected := 0
	n.MutableUpdate(query, func(node *CoreNodeItem) *CoreNodeItem {
		node.RemoveTags(tags_to_remove)
		node.AddTags(tags_to_add)
		records_affected += 1
		return node
	})
	return records_affected
}

func (n *CoreNodeItemStorage) MutableRebirthWithNewData(nodes []*CoreNodeItem) {
	n.MutableDrop()
	n.MutableCreate(nodes)
}
