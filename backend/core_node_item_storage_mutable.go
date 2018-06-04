package main

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
