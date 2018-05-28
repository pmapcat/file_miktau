package main

import (
	"errors"
	"strings"
)

func newCoreNodeItemStorage(core_dir string) CoreNodeItemStorage {
	return CoreNodeItemStorage{nodes: []*CoreNodeItem{}, core_dir: core_dir}
}

func (n *CoreNodeItemStorage) MutableUpdateInBulk(query CoreQuery, cb func(*CoreNodeItem) *CoreNodeItem) {
	for k, v := range n.nodes {
		if v.ApplyFilter(&query) {
			n.nodes[k] = cb(v)
		}
	}
}

func (c *CoreNodeItemStorage) RebirthWithNewData(new_data []*CoreNodeItem) {
	c.nodes = []*CoreNodeItem{}
	c.MutableAddMany(new_data)
}

func (c *CoreNodeItemStorage) MutableAddMany(data []*CoreNodeItem) {
	for _, item := range data {
		c.MutableAddNode(item.Tags, item.FilePath, item.Name, item.Modified.Day, item.Modified.Month, item.Modified.Year)
	}

}

func (n *CoreNodeItemStorage) GetNodesSorted(field string) []*CoreNodeItem {
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

	new_sortable := make([]*CoreNodeItem, len(n.nodes))
	copy(new_sortable, n.nodes)
	switch sfield {
	case "name":
		sort_slice(inverse, new_sortable, func(i, j int) bool {
			return new_sortable[i].Name < new_sortable[j].Name
		})
	case "modified":
		sort_slice(inverse, new_sortable, func(i, j int) bool {
			return new_sortable[i].ModifiedInDays() > new_sortable[j].ModifiedInDays()
		})
	}
	return new_sortable
}

func (n *CoreNodeItemStorage) GetThesaurus() map[string]int {
	thesaurus := map[string]int{}
	for _, node := range n.nodes {
		for _, tag := range node.Tags {
			thesaurus[tag] += 1
		}
	}
	return thesaurus
}

func (n *CoreNodeItemStorage) MutableAddNode(tags []string, fpath, fname string, day, month, year int) {
	// empty tags set has the tendency to look like this [""]
	if len(tags) == 1 && tags[0] == "" {
		tags = []string{}
	}
	n.nodes = append(n.nodes,
		&CoreNodeItem{Id: len(n.nodes), FilePath: fpath, Name: fname, Tags: undublicate_list(tags), Modified: CoreDateField{Year: year, Day: day, Month: month}})
}

func (n *CoreNodeItemStorage) MutableAddRemoveTagsToSelection(query CoreQuery, tags_to_add, tags_to_remove []string) int {
	records_affected := 0
	for nodeid, node := range n.nodes {
		if node.ApplyFilter(&query) {
			node.RemoveTags(tags_to_remove)
			node.AddTags(tags_to_add)
			node.Tags = undublicate_list(node.Tags)
			n.nodes[nodeid] = node
			records_affected += 1
		}
	}
	return records_affected
}

func (n *CoreNodeItemStorage) FSActionOnAListOfFiles(query CoreQuery, action string) error {
	fpathes := []string{}
	n.GetInBulk(query, func(item *CoreNodeItem) {
		fpathes = append(fpathes, item.FilePath)
	})
	switch action {
	case "symlinks":
		return fs_backend.OpenAsSymlinksInASingleFolder(fpathes)
	case "filebrowser":
		return fs_backend.OpenEachInFileExplorer(fpathes)
	case "default":
		return fs_backend.OpenEachInDefaultProgram(fpathes)
	}
	return errors.New("No action was specified for this dataset")
}

func (n *CoreNodeItemStorage) GetInBulk(query CoreQuery, cb func(*CoreNodeItem)) {
	for _, v := range n.nodes {
		if v.ApplyFilter(&query) {
			cb(v)
		}
	}
}

func (n *CoreNodeItemStorage) GetAppData(query CoreQuery) CoreAppDataResponse {
	wrf := query.WithResponseFields
	cloud_can_select := map[string]bool{}
	nodes_list := []*CoreNodeItem{}

	cloud := map[string]int{}
	if wrf.TreeTag || wrf.Cloud {
		cloud = n.GetThesaurus()
	}

	mpr := newTreeTag("root")
	calendar := &CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}
	calendar_can_select := &CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}

	// Gathering and processing
	if wrf.TreeTag || wrf.Calendar || wrf.CloudCanSelect || wrf.CalendarCanSelect || wrf.Nodes {
		for _, node := range n.GetNodesSorted(query.Sorted) {
			if wrf.TreeTag {
				get_app_data.GettingMpt(node, mpr, cloud)
			}
			if wrf.Calendar {
				get_app_data.GettingCalendar(node, calendar)
			}

			// check whether the result is drillable
			if node.ApplyFilter(&query) {
				if wrf.CloudCanSelect {
					get_app_data.PossibleTaggingDrillDowns(node, cloud_can_select)
				}
				if wrf.CalendarCanSelect {
					get_app_data.PossibleCalendarDrillDowns(node, calendar_can_select)
				}
				if wrf.Nodes {
					nodes_list = append(nodes_list, node)
				}
			}
		}
	}

	// amount of nodes
	total_nodes := len(nodes_list)
	// pagination
	left_slice, right_slice, pages_amount := PaginatorToSlice(total_nodes, query.PageSize, query.Page)
	nodes_list = nodes_list[left_slice:right_slice]

	// Formatting for the Rest output
	rsp := CoreAppDataResponse{}

	if wrf.NodeSorting {
		rsp.NodeSorting = query.Sorted
	}
	if wrf.TotalNodes {
		rsp.TotalNodes = uint32(total_nodes)
	}
	if wrf.Nodes {
		rsp.Nodes = nodes_list
	}
	if wrf.Calendar {
		rsp.Calendar = *calendar
	}
	if wrf.CalendarCanSelect {
		rsp.CalendarCanSelect = *calendar_can_select
	}
	if wrf.Cloud {
		rsp.Cloud = cloud
	}
	if wrf.CloudCanSelect {
		rsp.CloudCanSelect = cloud_can_select
	}
	if wrf.CoreDirectory {
		rsp.CoreDirectory = n.core_dir
	}
	if wrf.TreeTag {
		rsp.TreeTag = mpr
	}
	if wrf.DateNow {
		rsp.DateNow = dateNow()
	}
	if wrf.TotalNodesPages {
		rsp.TotalNodesPages = pages_amount
	}
	return rsp
}
