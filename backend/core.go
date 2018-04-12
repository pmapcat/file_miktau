package main

import (
	"sort"
	"strings"
)

// How does this work?
// * We locate term
// * We locate another term
// * And the third one
// * And then we iterate over every subelement

func (n *CoreNodeItem) TagRoot(thesaurus map[string]int) string {
	max := 0
	max_tag := ""
	for _, tag := range n.Tags {
		if thesaurus[tag] >= max {
			max_tag = tag
			max = thesaurus[tag]
		}
	}
	return max_tag
}
func (n *CoreNodeItem) ModifiedInDays() uint32 {
	if n._modified_days > 0 {
		return n._modified_days
	}
	n._modified_days = uint32(n.Modified.Day + n.Modified.Month*30 + n.Modified.Year*365)
	return n._modified_days
}

func (n *CoreNodeItem) ApplyFilter(c *CoreQuery) bool {
	// if query contains ids list, then it is likely that we
	// must filter by ids
	if len(c.Ids) > 0 {
		for _, id := range c.Ids {
			if n.Id == id {
				return true
			}
		}
		return false
	}
	// check equivalence for time
	if !(c.Modified.Year > 0 && n.Modified.Year == c.Modified.Year) {
		return false
	}
	if !(c.Modified.Day > 0 && n.Modified.Day == c.Modified.Day) {
		return false
	}
	if !(c.Modified.Month > 0 && n.Modified.Month == c.Modified.Month) {
		return false
	}
	// match nothing as everything
	if len(c.Tags) == 0 {
		return true
	}

	// and perform is_subset on tags
	for _, query_tag := range c.Tags {
		has_node_tag := false
		for _, this_node_tag := range n.Tags {
			if query_tag == this_node_tag {
				has_node_tag = true
				break
			}
		}
		if !has_node_tag {
			return false
		}
	}
	return true
}

func (n *CoreNodeItemStorage) MutableUpdateInBulk(query CoreQuery, cb func(*CoreNodeItem) *CoreNodeItem) {
	for k, v := range n.nodes {
		if v.ApplyFilter(&query) {
			n.nodes[k] = cb(v)
		}
	}
}
func newCoreNodeItemStorage() CoreNodeItemStorage {
	return CoreNodeItemStorage{nodes: []*CoreNodeItem{}}
}
// ================= > CONTINUE FROM HERE < =====================
func (n *CoreNodeItemStorage) GetNodesSorted(field string) []*CoreNodeItem {
	// check if initialized
	if n._nodes_sorted == nil {
		n._nodes_sorted = map[string][]*CoreNodeItem{}
	}
	// check whether cache is present
	nodes_list, ok := n._nodes_sorted[field]
	if ok {
		return nodes_list
	}

	// if not, sort it according to a preset
	inverse := false
	if strings.HasPrefix(field, "-") {
		inverse := true
		field = field[1:]
	}
	new_sortable
	switch field {
	case "name":
		sort.Slice(n.nodes, less func(i int, j int) bool{
			
		})
		
	case "modified":

	}

}

func (n *CoreNodeItemStorage) GetThesaurus() map[string]int {
	if n._thesaurus == nil {
		n._thesaurus = map[string]int{}
		for _, node := range n.nodes {
			for _, tag := range node.Tags {
				n._thesaurus[tag] += 1
			}
		}
	}
	return n._thesaurus

}
func (n *CoreNodeItemStorage) MutableAddNode(tags []string, fname string, day, month, year int) {
	n.nodes = append(n.nodes,
		&CoreNodeItem{Id: len(n.nodes) + 1, Name: fname, Tags: tags, Modified: CoreDateField{Year: year, Day: day, Month: month}})
}

func (n *CoreNodeItemStorage) GetInBulk(query CoreQuery, cb func(*CoreNodeItem)) {
	for k, v := range n.nodes {
		if v.ApplyFilter(&query) {
			cb(v)
		}
	}
}

func (n *CoreNodeItemStorage) GetAppData(query CoreQuery) CoreAppDataResponse {

	// most prominent cloud
	// a cloud, that is grouped under the most popular context/tag
	// on the dataset.
	// for more info on the algorithm look into
	// gokinate project on github
	cloud := map[string]map[string]int{}
	cloud_can_select := map[string]bool{}
	nodes_list := []*CoreNodeItem{}

	calendar := CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}
	calendar_can_select := CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}
	// Gathering and processing
	for nodeid, node := range n.nodes {
		// getting MPT
		tagroot := node.TagRoot(n.tag_thesaurus)
		_, ok := cloud[tagroot]
		if !ok {
			cloud[tagroot] = map[string]int{}
		}
		for _, tag := range node.Tags {
			cloud[tagroot][tag] += 1
		}

		// getting calendar
		calendar.Month[node.Modified.Month] += 1
		calendar.Day[node.Modified.Day] += 1
		calendar.Year[node.Modified.Year] += 1

		// check whether the result is drillable
		if node.ApplyFilter(&query) {
			// possible tagging drilldowns
			for _, tag := range node.Tags {
				cloud_can_select[tag] = true
			}
			// possible calendar drilldowns
			calendar_can_select.Month[node.Modified.Month] += 1
			calendar_can_select.Day[node.Modified.Day] += 1
			calendar_can_select.Year[node.Modified.Year] += 1
			nodes_list = append(nodes_list, node)
		}
	}

	// sorting nodes according to the sort parameter in a search query

	total_nodes := len(nodes_list)
	// this must be tested, because I have no idea what I am doing
	// here XD
	if len(nodes_list) > 100 {
		nodes_list = nodes_list[99:]
	}
	// Formatting for the Rest output
	rsp := CoreAppDataResponse{}
	rsp.NodeSorting = query.Sorted
	rsp.TotalNodes = uint32(total_nodes)
	rsp.Calendar = calendar
	rsp.CalendarCanSelect = calendar_can_select
	rsp.Cloud = cloud
	rsp.CloudCanSelect = cloud_can_select
	return rsp
}
