package main

import (
	"errors"
	log "github.com/sirupsen/logrus"
	"os"
	"strings"
)

type ResolverNodeItem struct {
	Node         *CoreNodeItem
	CameWithPath string
	WithResolver os.FileInfo
}

// for each file, check whether it is already in
// This is O(N*K) algorithm, where K is the amount of file_pathes.
// On larger systems, it might take larger time to work with it
func (n *CoreNodeItemStorage) ResolveIfPossibleWithinTheSystem(file_paths []string) ([]*ResolverNodeItem, []*ResolverNodeItem, error) {
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
		return []*ResolverNodeItem{}, []*ResolverNodeItem{}, nil
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
	unresolved := []*ResolverNodeItem{}
	for _, v := range finfos {
		if v != nil {
			unresolved = append(unresolved, v)
		}
	}
	return results, unresolved, nil
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
			return n.nodes[i].ModifiedInDays() > n.nodes[j].ModifiedInDays()
		})
	}
	return n.nodes
}

func (n *CoreNodeItemStorage) FSActionOnAListOfFiles(query CoreQuery, action int) error {
	fpathes := []string{}
	n.GetInBulk(query, func(item *CoreNodeItem) {
		fpathes = append(fpathes, item.FilePath)
	})
	switch action {
	case STRATEGY_SYMLINK:
		return fs_backend.OpenAsSymlinksInASingleFolder(fpathes)
	case STRATEGY_DEFAULT_PROGRAM:
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
			for _, tag := range node.MetaTags {
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
	// for _,node := range nodes_list {
	// 	node.ModifiedJson
	// }

	// Formatting for the Rest output
	rsp := CoreAppDataResponse{}

	rsp.Patriarchs = n.sorting_aggregator.GetPatriarchs()

	rsp.Cloud = mergers.MergeThesaurus(n.sorting_aggregator.GetThesaurus(), n.sorting_meta_aggregator.GetThesaurus())
	rsp.CloudContext = mergers.MergeTagContexts(n.sorting_aggregator.GetTagContext(), n.sorting_meta_aggregator.GetTagContext())
	rsp.CloudCanSelect = cloud_can_select

	rsp.NodeSorting = query.Sorted
	rsp.TotalNodes = total_nodes
	rsp.Nodes = nodes_list

	rsp.CoreDirectory = n.core_dir
	rsp.TotalNodesPages = pages_amount
	return rsp
}
