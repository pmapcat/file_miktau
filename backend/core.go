package main

import (
	// "log"
	"errors"
	"path/filepath"
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
		if thesaurus[tag] > max {
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

// will have to call only on mutable actions
func (n *CoreNodeItem) ApplyFilter(c *CoreQuery) bool {
	// if query contains filepathes list, then it is most likely
	// that we must filter by filepaths
	if len(c.FilePaths) > 0 {
		fname_fpath := filepath.Join(n.FilePath, n.Name)
		for _, fpath := range c.FilePaths {
			if fname_fpath == fpath {
				return true
			}
		}
		return false
	}

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

	// if year is not specified, than skip.
	// if year is specified and not matched, then the rest of the checking is meaningless
	// log.Println("Checking for year equivavalency")
	if c.Modified.Year > 0 && !(n.Modified.Year == c.Modified.Year) {
		return false
	}
	// the same for the month
	// log.Println("Checking for month equivavalency")
	if c.Modified.Month > 0 && !(n.Modified.Month == c.Modified.Month) {
		return false
	}

	// and the same for the day
	// log.Println("Checking for day equivavalency")
	if c.Modified.Day > 0 && !(n.Modified.Day == c.Modified.Day) {
		return false
	}

	// match all tags
	if len(c.Tags) == 0 {
		return true
	}

	// and perform is_subset on tags
	// log.Println("Checking for tag subsets")
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
		has_node_tag = false
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
func newCoreNodeItemStorage(core_dir string) CoreNodeItemStorage {
	return CoreNodeItemStorage{nodes: []*CoreNodeItem{}, core_dir: core_dir}
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
func sort_slice(inverse bool, slice interface{}, less func(i, j int) bool) {
	if inverse {
		sort.Slice(slice, func(i, j int) bool {
			return !less(i, j)
		})
		return
	}
	sort.Slice(slice, less)
	return
}

func (t *CoreQuery) WithTags(tags ...string) *CoreQuery {
	t.Tags = tags
	return t
}

func (t *CoreQuery) WithIds(ids ...int) *CoreQuery {
	t.Ids = ids
	return t
}
func (t *CoreQuery) OrderBy(sorted string) *CoreQuery {
	t.Sorted = sorted
	return t
}
func (t *CoreQuery) WithDate(year, month, day int) *CoreQuery {
	if year < 0 {
		year = 0
	}
	if month < 0 {
		month = 0
	}
	if day < 0 {
		day = 0
	}

	t.Modified = CoreDateField{Year: year, Month: month, Day: day}
	return t
}
func (t *CoreQuery) WithFilePathes(data ...string) *CoreQuery {
	t.FilePaths = data
	return t
}

func newCoreQuery() *CoreQuery {
	return &CoreQuery{
		Modified: CoreDateField{},
		Sorted:   "",
		Ids:      []int{},
		Tags:     []string{},
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
func (n *CoreNodeItem) AddTags(tags []string) {
	n.Tags = append(n.Tags, tags...)
}
func (n *CoreNodeItem) RemoveTags(tags []string) {
	result := []string{}
	for _, current_tag := range n.Tags {
		should_append := true
		for _, tag_to_remove := range tags {
			if current_tag == tag_to_remove {
				should_append = false
				break
			}
		}
		if should_append {
			result = append(result, current_tag)
		}
	}
	n.Tags = result
}
func newErrorCoreAppDataResponse(err error) CoreAppDataResponse {
	return CoreAppDataResponse{Error: err.Error()}
}
func newErrorModifyRecordsResponse(err error) ModifyRecordsRequest {
	return ModifyRecordsRequest{Error: err.Error()}
}

func newErrorBulkFileWorkage(err error) FileActionRequest {
	return FileActionRequest{Error: err}
}
func newErrorSwitchFoldersRequest(err error) SwitchFoldersRequest {
	return SwitchFoldersRequest{Error: err}
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
func (n *CoreNodeItem) IsTagged() bool {
	return len(n.Tags) > 0
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
	tag_thesaurus := n.GetThesaurus()

	calendar := CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}
	calendar_can_select := CoreDateFacet{Year: map[int]int{}, Month: map[int]int{}, Day: map[int]int{}}
	// Gathering and processing
	for _, node := range n.GetNodesSorted(query.Sorted) {
		// getting MPT
		if node.IsTagged() {
			tagroot := node.TagRoot(tag_thesaurus)
			_, ok := cloud[tagroot]
			if !ok {
				cloud[tagroot] = map[string]int{}
			}
			for _, tag := range node.Tags {
				cloud[tagroot][tag] += 1
			}
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
	rsp.Nodes = nodes_list
	rsp.Calendar = calendar
	rsp.CalendarCanSelect = calendar_can_select
	rsp.Cloud = cloud
	rsp.CloudCanSelect = cloud_can_select
	rsp.CoreDirectory = n.core_dir
	rsp.DateNow = dateNow()
	return rsp
}
