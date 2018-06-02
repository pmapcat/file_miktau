package main

import ()

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
	t.Tags = tags
	return t
}

// idempotent (i.e. can be called many times)
func (t *CoreQuery) OnAfterChange() {
	meta_tags, tags := split_meta_tags(t.Tags)
	t.Tags = tags
	t.MetaTags = meta_tags
}
