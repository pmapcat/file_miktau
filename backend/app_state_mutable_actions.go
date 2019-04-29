// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @ Copyright (c) Michael Leahcim                                                      @
// @ You can find additional information regarding licensing of this work in LICENSE.md @
// @ You must not remove this notice, or any other, from this software.                 @
// @ All rights reserved.                                                               @
// @@@@@@ At 2019-04-29 15:21 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@
package main

const (
	WRITE_TRANSACT_CREATE = iota
	WRITE_TRANSACT_UPDATE
	WRITE_TRANSACT_DELETE
)

// will have to happen on every <mutable> action on a database
// should nest without problem. `_transact` should bubble up to the highest
// level. It won't be called excessivly until highest level
func (c *AppState) _set_is_transact(is_transact bool) {
	c._transact_happening = is_transact
}

func (c *AppState) _transact(action int, worker func() []*AppStateItem) {
	if c._transact_happening {
		worker()
		return
	}
	c._set_is_transact(true)

	// in case of error/panic or other premature exit.
	defer c._set_is_transact(false)

	applied_items := worker()
	switch action {
	case WRITE_TRANSACT_CREATE:
		for _, v := range c._on_after_create {
			v(applied_items)
		}
	case WRITE_TRANSACT_UPDATE:
		for _, v := range c._on_after_update {
			v(applied_items)
		}
	}
	for _, action := range c._call_after_update {
		action.OnBeforeRun(c)
	}
	for _, action := range c._call_after_update {
		for _, v := range c.nodes {
			action.Accumulate(v)
		}
	}
	for _, action := range c._call_after_update {
		for _, v := range c.nodes {
			action.Aggregate(v)
		}
	}
	for _, action := range c._call_after_update {
		action.OnAfterRun(c)
	}
}

func (n *AppState) MutableCreate(nodes []*AppStateItem) []int {
	new_ids := []int{}
	n._transact(WRITE_TRANSACT_CREATE, func() []*AppStateItem {
		result := []*AppStateItem{}
		for _, node := range nodes {
			node.Id = len(n.nodes)
			n.nodes = append(n.nodes, node)
			new_ids = append(new_ids, node.Id)
			result = append(result, node)
		}
		return result
	})
	return new_ids
}

func (n *AppState) MutableUpdate(query Query, cb func(*AppStateItem) *AppStateItem) {
	n._transact(WRITE_TRANSACT_UPDATE, func() []*AppStateItem {
		result := []*AppStateItem{}
		for k, v := range n.nodes {
			if v.ApplyFilter(&query) {
				n.nodes[k] = cb(v)
				result = append(result, v)
			}
		}
		return result
	})
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

func (n *AppStateSync) MutableSwitchFolders(new_root string) error {
	ap, err := NewAppStateOnFolder(new_root, AppStateItemIdentity)
	if err != nil {
		return err
	}
	n.aps = ap
	return nil
}
