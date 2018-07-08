package main

import (
	"github.com/go-test/deep"
	"github.com/stretchr/testify/assert"
	"os"
	"strings"
	"testing"
)

const DEMO_DATASET_TEMP_FOLDER = "simple_in_dir"

// Returns whether the assertion was successful (true) or not (false).
func MikEqual(t assert.TestingT, actual, expected interface{}, msgAndArgs ...interface{}) bool {
	if !assert.ObjectsAreEqual(expected, actual) {
		if diff := deep.Equal(expected, actual); diff != nil {
			assert.Fail(t, strings.Join(diff, "\n"), msgAndArgs...)
		}
	}
	return true
}

func withAnyInDir(t *testing.T, repeat int, ds []*AppStateItem, root_dir string, cb func(*AppState)) {
	nodes_list := []*AppStateItem{}
	for i := 0; i < repeat; i++ {
		nodes_list = append(nodes_list, ds...)
	}
	assert.Equal(t, TestBuildProjectFolderOnDataSet(root_dir, nodes_list), nil)
	aps, err := NewAppStateOnFolder(root_dir, AppStateItemIdentity)
	assert.Equal(t, err, nil)
	cb(aps)
	assert.Equal(t, TearDownProjectOnDataSet(root_dir), nil)
}

func WithDachaInDir(t *testing.T, repeat int, cb func(*AppState)) {
	withAnyInDir(t, repeat, buildDachaDataset(), DEMO_DATASET_TEMP_FOLDER, cb)
}

func WithSimpleInDir(t *testing.T, repeat int, cb func(*AppState)) {
	withAnyInDir(t, repeat, buildDemoDataset(), DEMO_DATASET_TEMP_FOLDER, cb)
}

func TearDownProjectOnDataSet(root_dir string) error {
	return os.RemoveAll(root_dir)
}

func TestBuildingAppStateOnFS(t *testing.T) {
	WithSimpleInDir(t, 1, func(cnis *AppState) {
		NewAppStateResponse(cnis, *newQuery()).MetaCloud()
		assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloud(),
			map[string]int{"work": 20, "bibliostore": 8, "translator": 2, "natan": 13, "wiki": 1, "everybook": 1,
				"amazon": 2, "согласовать": 1, "moscow_market": 9, "sforim": 2, "скачка_источников": 1, "биржа": 2,
				"магазины": 2, "UI": 1, "personal": 4, "blog": 1, "devops": 1, "zeldin": 2, "usecases": 2, "работа_сделана": 1})
		assert.Equal(t, len(cnis.nodes), 22)
	})
}

func TestReflectionOnDataModification(t *testing.T) {
	WithSimpleInDir(t, 1, func(cnis *AppState) {
		res, err := fs_backend.MoveOnRootGivenTags(cnis.core_dir, jp(cnis.core_dir, "work/natan/bibliostore/translator/blob.mp4"), []string{"hello", "world", "near", "end"})
		assert.Equal(t, err, nil)
		assert.Equal(t, res, jp(cnis.core_dir, "hello/world/near/end/blob.mp4"))
		assert.Equal(t, MustIsFileExist(jp(cnis.core_dir, "hello/world/near/end/blob.mp4")), true)
		assert.Equal(t, MustIsFileExist(jp(cnis.core_dir, "work/natan/bibliostore/translator/blob.mp4")), false)
	})
	WithSimpleInDir(t, 1, func(cnis *AppState) {
		res, err := fs_backend.MoveOnRootGivenTags(cnis.core_dir, jp(cnis.core_dir, "work/natan/bibliostore/translator/blob.mp4"), []string{})
		assert.Equal(t, err, nil)
		assert.Equal(t, res, jp(cnis.core_dir, "blob.mp4"))
		assert.Equal(t, MustIsFileExist(jp(cnis.core_dir, "blob.mp4")), true)
		assert.Equal(t, MustIsFileExist(jp("work/natan/bibliostore/translator/blob.mp4")), false)
	})
}

func TestSymlinkInRootGivenForeignPathes(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		touch(t, jp(temp_dir, "a_file.mp4"))
		touch(t, jp(temp_dir, "a_file.docx"))
		touch(t, jp(temp_dir, "a_file.brabus"))

		WithSimpleInDir(t, 1, func(cnis *AppState) {
			new_paths, err := fs_backend.SymlinkInRootGivenForeignPathes(DEMO_DATASET_TEMP_FOLDER, []string{
				jp(temp_dir, "a_file.mp4"),
				jp(temp_dir, "a_file.docx"),
				jp(temp_dir, "a_file.docx"),
			})
			assert.Equal(t, err, nil)
			assert.Equal(t, new_paths, []string{
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file.docx"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file_1.docx")})

			// do the same, but files now are within root
			new_paths, err = fs_backend.SymlinkInRootGivenForeignPathes(DEMO_DATASET_TEMP_FOLDER, []string{
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file.docx"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file_1.docx"),
			})
			assert.Equal(t, err, nil)
			assert.Equal(t, new_paths, []string{
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file_1.mp4"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file_2.docx"),
				jp(DEMO_DATASET_TEMP_FOLDER, "a_file_1_1.docx")})

		})
	})

}

func TestBuildSymlinksInADefaultProgram(t *testing.T) {
	WithSimpleInDir(t, 1, func(cnis *AppState) {
		fpathes := []string{}
		for _, v := range cnis.nodes {
			fpathes = append(fpathes, v.FilePath)
		}
		temp_dir, err := fs_backend.SymlinkInTempGivenPathes(fpathes)
		assert.Equal(t, err, nil)
		assert.Equal(t, len(fs_ls(temp_dir).Files), 22)
		assert.Equal(t, len(fs_backend.getTempDirsCreated()), 1)
		fs_backend.DropTempDirsCreated()
	})
}
