# The friendly taxonomy system

![Begin of the app](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/cloud.png)

This program is an attempt (a research?) to make 
user friendly taxonomy building. 

This program uses what it has, i.e. an existing
folder hierarchy tools as its database.

That is why, here goes a one **!!VERY IMPORTANT NOTICE!!**:

```bash

!!DO NOT WORK ON IMPORTANT DATA WITHOUT MAKING BACKUP FIRST!!
This program is work in progress. It will lose your data 

```

## How to run

```bash

git clone https://github.com/MichaelLeachim/file_miktau;
cd file_miktau;
tmuxinator .;

```

## Concepts 

### Tags

The Tag is similar to a folder, but does not necessarily exists in a hierarchy
of folders. 

#### Tag sets

Tags form mutually exclusive families. I.e. when you drill down on a given tag, 
there is a set of possible other tags to drill within the list of nodes that contain
a given tag. For example:

```bash
alive, grapefruit
alive, animals, mushroom, amanita, inedible
alive, animals, mushroom, boletus, edible
plants, grapefruit, alive, green, edible
brown, hedgehog, animal, alive,
stone, big
```

[TODO] Show hierarchy
[TODO] Show family
[TODO] Show relations

#### Tag trees

### Selections 

![Selection](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/selection.png)
Selection is a way to narrow down a set of tags:
* When one selects a tag
  * Tags that are mutually exluded become inactive. Clicking on exclusion tag, will select it instead of current selection
  * Tags that are possible drill downs are active. On click, tag drilldown 
  
### Autocomplete   
![Autocomplete](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/complete.png)

Autocomplete shows:
* Current tag, that is possible to drill down on
* Related tags that are related with each other through the set of nodes within this tag

### Cloud
![Cloud view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/cloud.png)

### Edit view
![Edit view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/edit_view2.png)

### Tree list view
![Tree list view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/tree.png)

### Autotags
![Autotags](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/autotags.png)

### FS mapping

* Open in folder (will symlink all files related to a given tag selection into a folder)
* Add files (will symlink new files into the chosen root folder)



