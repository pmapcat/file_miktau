package main

import (
	"errors"
	"strings"
)

func newCoreNodeItemStorage(core_dir string) CoreNodeItemStorage {
	return CoreNodeItemStorage{nodes: []*CoreNodeItem{}, core_dir: core_dir}
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
	cloud_can_select := map[string]bool{}
	nodes_list := []*CoreNodeItem{}

	// Gathering and processing
	for _, node := range n.GetNodesSorted(query.Sorted) {
		// check whether the result is drillable
		if node.ApplyFilter(&query) {
			// cloud can select
			for _, tag := range node.Tags {
				cloud_can_select[tag] = true
			}
			nodes_list = append(nodes_list, node)
		}
	}
	// amount of nodes
	total_nodes := len(nodes_list)
	// pagination
	left_slice, right_slice, pages_amount := PaginatorToSlice(total_nodes, query.PageSize, query.Page)
	nodes_list = nodes_list[left_slice:right_slice]

	// Formatting for the Rest output
	rsp := CoreAppDataResponse{}

	rsp.Patriarchs = n.sorting_aggregator.GetPatriarchs()
	rsp.Cloud = n.sorting_aggregator.GetThesaurus()
	rsp.CloudContext = n.sorting_aggregator.GetTagContext()

	rsp.NodeSorting = query.Sorted
	rsp.TotalNodes = uint32(total_nodes)
	rsp.Nodes = nodes_list
	rsp.CloudCanSelect = cloud_can_select
	rsp.CoreDirectory = n.core_dir
	rsp.TotalNodesPages = pages_amount
	return rsp
}
