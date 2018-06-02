package main

import (
	// "fmt"
	// "strconv"
	"time"
)

func undublicate_list(list []string) []string {
	checker := map[string]bool{}
	result := []string{}
	for _, v := range list {
		if checker[v] {
			continue
		}
		result = append(result, v)
		checker[v] = true
	}
	return result
}

// will populate meta tags of a node.
// important! all hooks should be idempotent
func HookOnAfterChange(m *CoreNodeItem) {

	// undublicate tags
	m.Tags = undublicate_list(m.Tags)

	// empty tags set has the tendency to look like this [""]
	if len(m.Tags) == 1 && m.Tags[0] == "" {
		m.Tags = []string{}
	}

	m.MetaTags = []string{}
	// add empty
	if len(m.Tags) == 0 {
		m.MetaTags = append(m.MetaTags, "@empty")
	}

	// recency
	date := time.Now()
	delta := date.Sub(m.Modified)
	deltaHours := delta.Hours()

	if deltaHours < 24 {
		m.MetaTags = append(m.MetaTags, "@modified:today")
	}
	if deltaHours < 168 {
		m.MetaTags = append(m.MetaTags, "@modified:this-week")
	}
	if deltaHours < 672 {
		m.MetaTags = append(m.MetaTags, "@modified:this-month")
	}
	// how can I do smth. like: @modified:100-oldest-files
	//   * fill element into list of 100 (if not full)
	//   * if full, I would add current and pop smallest
	//   * then, it will automatically become what I need
	//   * thus, priority queue it is. With methods:
	//     * .pop() => "blab"
	//     * .add(item,rank) => nil
	//     * .count() => 23
	//     * .into_list() => []string{}

	if deltaHours < 8064 {
		m.MetaTags = append(m.MetaTags, "@modified:this-year")
	}
	if deltaHours > 8064 {
		m.MetaTags = append(m.MetaTags, "@modifed:long-ago")
	}
	// file size
	if m.FileSizeInMb < 1 {
		m.MetaTags = append(m.MetaTags, "@file-size:less than 1mb")
	}
	if m.FileSizeInMb < 10 && m.FileSizeInMb > 1 {
		m.MetaTags = append(m.MetaTags, "@file-size:1—10mb")
	}
	if m.FileSizeInMb < 50 && m.FileSizeInMb > 10 {
		m.MetaTags = append(m.MetaTags, "@file-size:10—50mb")
	}
	if m.FileSizeInMb < 100 && m.FileSizeInMb > 50 {
		m.MetaTags = append(m.MetaTags, "@file-size:50—100mb")
	}
	if m.FileSizeInMb < 500 && m.FileSizeInMb > 100 {
		m.MetaTags = append(m.MetaTags, "@file-size:100—500mb")
	}
	if m.FileSizeInMb < 1000 && m.FileSizeInMb > 500 {
		m.MetaTags = append(m.MetaTags, "@file-size:500-1000mb")
	}
	if m.FileSizeInMb > 1000 {
		m.MetaTags = append(m.MetaTags, "@file-size:more than 1000mb")
	}
	// file type (a.k.a. extension)
	withExt("@file-type:image", []string{".tif", ".tiff", ".gif", ".jpeg", ".jpg", ".nef", ".png", ".psd", ".bmp"}, m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:video", []string{".avi", ".mkv", ".mp4", ".wmv"}, m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:audio", []string{".mp3", ".m3u"}, m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:document", []string{".txt", ".doc", ".docx", ".html", ".rtf", ".odt", ".pdf", ".djvu", ".xls", ".xlsx", ".doc", ".docx", ".ppt", ".pptx"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:microsfot-office-document",
		[]string{".xls", ".xlsx", ".doc", ".docx", ".ppt", ".pptx"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:presentation",
		[]string{".ppt", ".pptx"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:spreadsheet",
		[]string{".csv", ".xls", ".xlsx", ".ods"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:archive",
		[]string{".tar", ".tar.gz", ".gz", ".rar", ".zip", ".bz", ".tar.bz"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:ebook",
		[]string{".pdf", ".djvu", ".fb2", ".epub", ".maff"},
		m.MetaTags, m.FileExtensionLowerCased)
	withExt("@file-type:programming",
		[]string{".rb", ".clj", ".cljs", ".py", ".sh", ".conf", ".go", ".json", ".c"},
		m.MetaTags, m.FileExtensionLowerCased)
}

func CallHooks(n *CoreNodeItem) {
	HookOnAfterChange(n)

}
