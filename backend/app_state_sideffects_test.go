package main

import (
	"github.com/stretchr/testify/assert"
	"os"
	"path/filepath"
	"testing"
)

// creates empty file under directory
func touch(t *testing.T, fname string) {
	assert.Equal(t, os.MkdirAll(filepath.Dir(fname), DEFAULT_PERMISSION), nil)
	f, err := os.Create(fname)
	if err == nil {
		defer f.Close()
	}
	assert.Equal(t, err, nil)
}

func TestSideEffectMutablePushNewFiles(t *testing.T) {
	WithSimpleInDir(t, 1, func(cnis *AppState) {
		resolved, unresolved, err := cnis.SideEffectResolveIfPossibleWithinFileSystem([]string{
			jp(DEMO_DATASET_TEMP_FOLDER, "blab.mp4"),
			jp(DEMO_DATASET_TEMP_FOLDER, "work/natan/bibliostore/translator/blob.mp4"),
			"main.go",
		})
		assert.Equal(t, err, nil)
		assert.Equal(t, len(resolved), 2)
		assert.Equal(t, len(unresolved), 1)
		assert.Equal(t, resolved[0].CameWithPath, jp(DEMO_DATASET_TEMP_FOLDER, "blab.mp4"))
		assert.Equal(t, resolved[0].WithResolver.Name(), "blab.mp4")
		assert.Equal(t, resolved[0].Node.Name, "blab.mp4")
		assert.Equal(t, resolved[0].Node.Id, 0)
		assert.Equal(t, resolved[1].Node.Id, 6)
	})
}

func TestSideEffectMutabablePushNewFiles(t *testing.T) {
	// test uploading data that doesn't already exist
	WithTempDir(t, func(temp_path string) {
		touch(t, jp(temp_path, "some_demo_file.mp4"))
		touch(t, jp(temp_path, "another_file.mp4"))
		touch(t, jp(temp_path, "blab.mp4"))
		touch(t, jp(temp_path, "mordor.mp4"))

		WithSimpleInDir(t, 1, func(cnis *AppState) {
			assert.Equal(t, fs_backend.ln_s(
				jp(DEMO_DATASET_TEMP_FOLDER, "work/everybook/dramo.mp4"),
				jp(temp_path, "new_file.mp4")), nil)

			reso, err := cnis.SideEffectMutablePushNewFiles([]string{
				// one file that is added from the external source
				jp(temp_path, "some_demo_file.mp4"),
				// one file that repeats (by name) the file that already exists in the system
				jp(temp_path, "blab.mp4"),
				// one file, that is already within the system, but which symlinked to the outside system
				// to not to clash it within
				jp(temp_path, "new_file.mp4"),
				// one file that doesn't exist
				jp(temp_path, "gornitsa"),
			})
			assert.Equal(t, err, nil)
			assert.Equal(t, len(reso), 3)
			bo := []string{}
			for _, v := range NewAppStateResponse(cnis, *newQuery().WithIds(reso...)).Nodes {
				bo = append(bo, v.FilePath)
			}
			assert.Equal(t, bo, []string{
				jp(DEMO_DATASET_TEMP_FOLDER, "some_demo_file.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "blab_1.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "work/everybook/dramo.mp4"),
			})

			// now, the same resolution within the root, should yield the same files (without dublicated copies)
			// basically, test should yield the same results. (but with one new file(for the sake of it))
			reso, err = cnis.SideEffectMutablePushNewFiles([]string{
				jp(temp_path, "some_demo_file.mp4"),
				jp(temp_path, "another_file.mp4"),
				jp(temp_path, "blab.mp4"),
				jp(temp_path, "gornitsa"),
			})
			assert.Equal(t, err, nil)
			assert.Equal(t, len(reso), 3)
			bo = []string{}
			for _, v := range NewAppStateResponse(cnis, *newQuery().WithIds(reso...)).Nodes {
				bo = append(bo, v.FilePath)
			}
			assert.Equal(t, bo, []string{
				jp(DEMO_DATASET_TEMP_FOLDER, "some_demo_file.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "another_file.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "blab_1.mp4"),
			})

		})

	})
}
