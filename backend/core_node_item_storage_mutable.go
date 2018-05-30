package main

import (
	"time"
)

func (n *CoreNodeItemStorage) MutableUpdateInBulk(query CoreQuery, cb func(*CoreNodeItem) *CoreNodeItem) {
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

func (c *CoreNodeItemStorage) MutableNewDataSet(new_data []*CoreNodeItem) {
	c._transact(func() {
		c.nodes = []*CoreNodeItem{}
		c.MutableAddMany(new_data)
	})
}

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
	for _, v := range c.nodes {
		c.sorting_aggregator.Accumulate(v)
	}
	for k, v := range c.nodes {
		c.sorting_aggregator.Aggregate(v)
	}
	c._transact_happening = false

}

func (c *CoreNodeItemStorage) MutableAddMany(data []*CoreNodeItem) {
	c._transact(func() {
		for _, item := range data {
			c.MutableAddNode(item.Tags, item.FilePath, item.Name, item.Modified.Day(), item.Modified.Month(), item.Modified.Year())
		}
	})
}

func (n *CoreNodeItemStorage) MutableAddNode(tags []string, fpath, fname string, day, month, year int) {
	n._transact(func() {
		new_node_item := &CoreNodeItem{Id: len(n.nodes), FilePath: fpath, Name: fname, Tags: tags, Modified: time.Date(year, time.Month(month), day)}
		CallHooks(new_node_item)
		n.nodes = append(n.nodes, new_node_item)
	})
}

func (n *CoreNodeItemStorage) MutableAddRemoveTagsToSelection(query CoreQuery, tags_to_add, tags_to_remove []string) int {
	records_affected := 0
	n._transact(func() {
		for nodeid, node := range n.nodes {
			if node.ApplyFilter(&query) {
				node.RemoveTags(tags_to_remove)
				node.AddTags(tags_to_add)
				CallHooks(node)
				n.nodes[nodeid] = node
				records_affected += 1
			}
		}
	})
	return records_affected
}
