package main

import ()

func newCoreQuery() *CoreQuery {
	return &CoreQuery{
		Modified:           CoreDateField{},
		PageSize:           DEFAULT_PAGE_SIZE,
		Page:               1,
		Sorted:             "",
		Ids:                []int{},
		Tags:               []string{},
		WithResponseFields: WithCoreAppDataResponseFields{},
	}
}

func newCoreQueryWithFullResponse() *CoreQuery {
	return newCoreQuery().WithFullResponse()
}

func (t *CoreQuery) WithFullResponse() *CoreQuery {
	t.WithResponseFields = WithCoreAppDataResponseFields{
		NodeSorting:       true,
		TotalNodes:        true,
		TotalNodesPages:   true,
		CoreDirectory:     true,
		DateNow:           true,
		Nodes:             true,
		CloudCanSelect:    true,
		Cloud:             true,
		CloudContext:      true,
		TreeTag:           true,
		CalendarCanSelect: true,
		Calendar:          true,
	}
	return t
}

func (t *CoreQuery) WithFilePathes(data ...string) *CoreQuery {
	t.FilePaths = data
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
func (t *CoreQuery) WithPage(page int) *CoreQuery {
	t.Page = page
	return t
}
func (t *CoreQuery) WithPageSize(pagesize int) *CoreQuery {
	t.PageSize = pagesize
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

func (t *CoreQuery) WithTags(tags ...string) *CoreQuery {
	t.Tags = tags
	return t
}
