package main

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

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
