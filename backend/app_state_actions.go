// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @ Copyright (c) Michael Leahcim                                                      @
// @ You can find additional information regarding licensing of this work in LICENSE.md @
// @ You must not remove this notice, or any other, from this software.                 @
// @ All rights reserved.                                                               @
// @@@@@@ At 2019-04-29 15:20 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

package main

import (
	"strings"
)

func (n *AppState) GetInBulk(query Query, cb func(*AppStateItem)) {
	for _, v := range n.nodes {
		if v.ApplyFilter(&query) {
			cb(v)
		}
	}
}
func (n *AppState) GetItems() []*AppStateItem {
	return n.nodes
}

func (n *AppState) GetItemsSorted(field string) []*AppStateItem {
	// if no sort order was specified
	if field == "" {
		return n.nodes
	}
	// if not, sort it according to a preset
	inverse := false
	sfield := field
	if strings.HasPrefix(field, "-") {
		inverse = true
		sfield = field[1:]
	}
	// why I should preserve sorting order of default dataset?
	// because of getting by id/a.k.a. getting by node?
	// well, there was no usecase for it, as of yet.
	// only by id getting is for mutable operations,
	// and I, as well, can just do it in a fullscan fashion
	switch sfield {
	case "name":
		sort_slice(inverse, n.nodes, func(i, j int) bool {
			return n.nodes[i].Name < n.nodes[j].Name
		})
	case "modified":
		sort_slice(inverse, n.nodes, func(i, j int) bool {
			return n.nodes[i].Modified.Time().Sub(n.nodes[j].Modified.Time()) > 0
		})
	}
	return n.nodes
}
