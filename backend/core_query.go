package main

import ()

func (t *CoreQuery) GetTags() []string {
	if t._getter_on_tags_is_already_called {
		return t._standart_tags
	}
	t._split_meta()
	return t._standart_tags
}
func (t *CoreQuery) _split_meta() {
	t._meta_tags, t._standart_tags = split_meta_tags(t.BaseTags)
	t._getter_on_tags_is_already_called = true
}
func (t *CoreQuery) GetMetaTags() []string {
	if t._getter_on_tags_is_already_called {
		return t._meta_tags
	}
	t._split_meta()
	return t._meta_tags

}

func (t *CoreQuery) WithFilePathes(data ...string) *CoreQuery {
	t.FilePaths = data
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
	t.BaseTags = tags
	return t
}
