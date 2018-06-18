package main

type AppStateResponse struct {
	Error           string                    `json:"error"`
	NodeSorting     string                    `json:"nodes-sorted"`
	TotalNodes      int                       `json:"total-nodes"`
	TotalNodesPages int                       `json:"total-nodes-pages"`
	CoreDirectory   string                    `json:"core-directory"`
	Nodes           []*AppStateItem           `json:"nodes"`
	Patriarchs      []string                  `json:"patriarchs"`
	CloudCanSelect  map[string]bool           `json:"cloud-can-select"`
	Cloud           map[string]int            `json:"cloud"`
	CloudContext    map[string]map[string]int `json:"cloud-context"`
}

func newAppStateResponse(n *AppState, query Query) AppStateResponse {
	cloud_can_select := map[string]bool{}
	nodes_list := []*AppStateItem{}

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
	rsp := AppDataResponse{}

	rsp.Patriarchs = n.sorting_aggregator.GetPatriarchs()

	rsp.Cloud = MergeThesaurus(n.sorting_aggregator.GetThesaurus(), n.sorting_meta_aggregator.GetThesaurus())
	rsp.CloudContext = MergeTagContexts(n.sorting_aggregator.GetTagContext(), n.sorting_meta_aggregator.GetTagContext())
	rsp.CloudCanSelect = cloud_can_select

	rsp.NodeSorting = query.Sorted
	rsp.TotalNodes = total_nodes
	rsp.Nodes = nodes_list

	rsp.CoreDirectory = n.core_dir
	rsp.TotalNodesPages = pages_amount
	return rsp

}

func (c AppStateResponse) MetaCloud() map[string]int {
	meta_cloud := map[string]int{}
	for k, v := range c.Cloud {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c AppStateResponse) MetaCloudContext() map[string]map[string]int {
	meta_cloud := map[string]map[string]int{}
	for k, v := range c.CloudContext {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c AppStateResponse) MetaCloudCanSelect() map[string]bool {
	meta_cloud := map[string]bool{}
	for k, v := range c.CloudCanSelect {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c AppStateResponse) SimpleCloud() map[string]int {
	meta_cloud := map[string]int{}
	for k, v := range c.Cloud {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c AppStateResponse) SimpleCloudContext() map[string]map[string]int {
	meta_cloud := map[string]map[string]int{}
	for k, v := range c.CloudContext {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c AppStateResponse) SimpleCloudCanSelect() map[string]bool {
	meta_cloud := map[string]bool{}
	for k, v := range c.CloudCanSelect {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}
