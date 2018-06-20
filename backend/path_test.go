package main

import (
	"github.com/stretchr/testify/assert"
	"log"
	"os"
	"path/filepath"
	"strings"
	"testing"
)

func TestDepthFirstWalkTraversal(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		most_top := jp(temp_dir, "hello/world/")
		// creating stuff
		assert.Equal(t, fs_backend.Create(jp(most_top, "new/and/large/data/input/point/blab", "dorothy.mp4")), nil)
		assert.Equal(t, fs_backend.Create(jp(most_top, "zanzibar/is/a/good/country", "dorothy.mp4")), nil)
		assert.Equal(t, fs_backend.Create(jp(most_top, "world/domination/must/be/ensured", "dorothy.mp4")), nil)
		assert.Equal(t, fs_backend.Create(jp(most_top, "world/is/at/peace", "dorothy.mp4")), nil)
		assert.Equal(t, fs_backend.Create(jp(most_top, "world/is/at/peace/with/a/lot/of/people", "dorothy.mp4")), nil)

		zobo := []string{}
		filepath.Walk(temp_dir, func(path string, info os.FileInfo, err error) error {
			zobo = append(zobo, path)
			return nil
		})
		log.Println("\n" + strings.Join(zobo, "\n"))
		assert.Equal(t, zobo, "In depth first fashion should go")

	})

}
