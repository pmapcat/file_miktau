package main

type Query struct {
	Sorted                            string   `json:"sorted"`
	PageSize                          int      `json:"page-size"`
	Page                              int      `json:"page"`
	FilePaths                         []string `json:"file-paths"`
	Ids                               []int    `json:"ids"`
	BaseTags                          []string `json:"tags"`
	_getter_on_tags_is_already_called bool
	_standart_tags                    []string `json:"-"`
	_meta_tags                        []string `json:"-"`
}

func newQuery() *Query {
	return &Query{
		PageSize: DEFAULT_PAGE_SIZE,
		Page:     1,
		Sorted:   "",
		Ids:      []int{},
		BaseTags: []string{},
	}
}

func (t *Query) GetTags() []string {
	if t._getter_on_tags_is_already_called {
		return t._standart_tags
	}
	t._split_meta()
	return t._standart_tags
}

func (t *Query) _split_meta() {
	t._meta_tags, t._standart_tags = split_meta_tags(t.BaseTags)
	t._getter_on_tags_is_already_called = true
}
func (t *Query) GetMetaTags() []string {
	if t._getter_on_tags_is_already_called {
		return t._meta_tags
	}
	t._split_meta()
	return t._meta_tags

}

func (t *Query) WithFilePathes(data ...string) *Query {
	t.FilePaths = data
	return t
}

func (t *Query) WithPage(page int) *Query {
	t.Page = page
	return t
}
func (t *Query) WithPageSize(pagesize int) *Query {
	t.PageSize = pagesize
	return t
}

func (t *Query) WithIds(ids ...int) *Query {
	t.Ids = ids
	return t
}
func (t *Query) OrderBy(sorted string) *Query {
	t.Sorted = sorted
	return t
}

func (t *Query) WithTags(tags ...string) *Query {
	t.BaseTags = tags
	return t
}
