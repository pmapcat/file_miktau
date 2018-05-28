package main

import (
	"sort"
)

func newTreeTag(name string) *TreeTag {
	return &TreeTag{Name: name, Children: map[string]*TreeTag{}}
}

func (n *TreeTag) ChildrenList() []*TreeTag {
	po := []*TreeTag{}
	for _, v := range n.Children {
		po = append(po, v)
	}
	sort.Slice(po, func(i int, j int) bool {
		return po[i].Name < po[j].Name
	})
	return po
}

// no need for performance here
// doing naive string concat
func (n *TreeTag) Show(space string) string {
	result := ""
	result += space + n.Name + " \n"
	for _, item := range n.ChildrenList() {
		result += item.Show(space + "-")
	}
	return result

}
