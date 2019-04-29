# The friendly taxonomy system

![Begin of the app](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/cloud.png)

This program is an attempt (a research?) to make 
user friendly taxonomy building

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

### Tag

The Tag is similar to a folder, but does not necessarily exists in a hierarchy
of folders. Tag can be attached to many nodes

### Node
Node is a file item. Node can have many tags

#### Tag sets

Tags form mutually exclusive families. I.e. when you drill down on a given tag, 
there is a set of possible other tags to drill within the list of nodes that contain
a given tag. 

For example, let's say that we have the following tags

```bash
alive, grapefruit
alive, animals, mushroom, amanita, inedible
alive, animals, mushroom, boletus, edible
plants, grapefruit, alive, green, edible
brown, hedgehog, animal, alive,
stone, big
```

So, we have two groups(families) of tags
```bash
alive, grapefruit, animals, mushroom, edible,inedible,brown, hedgehog e.t.c.

And 

stone, big
```

These groups are not related to each other in any way. 

#### Node sets

A node set is a selection of nodes that contain given tag, or several
given tags. When there is no selection on two given tags, then those tags are mutually 
exclusive 

Node set can:
* Be shown in folder (via symlinking)
* Have a tag added to it (through edit menu)
* Have a tag removed from it (through edit menu)


#### Tag trees

Let's say, that we have the same tag groups as defined above. 
If we want to show them as a tree, where larger groups will 
contain smaller sub groups like this

```shell
animals
  alive
    edible
     grapefruit
     boletus
    inedible
     amanita
     hedgehog
 inedible 
big
 stone
e.t.c. 
```

### Selections 

![Selection](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/selection.png)
Selection is a way to narrow down a set of tags:
* When one selects a tag
  * Tags that are mutually exluded become inactive. 
    Clicking on exclusion tag, will select it instead of current selection
  * Tags that are possible drill downs are active. On click, tag drilldown 
  
### Autocomplete   
![Autocomplete](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/complete.png)

Autocomplete shows tag and its family of tags:
 * Current tag, that is possible to drill down on
 * Related tags that are related with each other 
   through the set of nodes within this tag
   
### Cloud
![Cloud view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/cloud.png)

Cloud shows all tags, or, when selected, it shows current family of tags. 
All mutually exclusive families of tags are shown disabled when 
given family is selected. 

### Edit view
![Edit view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/edit_view2.png)

Edit view is a bulk tagging mechanism. It allows:
  * Removing old tags for a set of nodes
  * Adding new tags
  * Attaching node to a tag family
  
There are other, higher order operations on selections:
  * Joining families   (through connecting a tag from other family to current selection)
  * Splitting families (through removing the linking tag from a given node set)
  * Renaming tags, through removal and addition
  
### Tree list view
![Tree list view](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/tree.png)

Tree list view allows to see files (or nodes), in a traditional list form. It shows tags 
in the form of the tree, disabling those parts of the path that are already shown on the shown. 
So, it will be easier to see tag tree behind visual cluter

It is possible, in this view, to select node items separately and tag them in smaller, non 
previously defined, groups

### Autotags
![Autotags](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/autotags.png)

Autotags are a family of tags that has a prefix `@`
You can look at all available `@` tags through search, like this: 
![Autotags 2](https://github.com/MichaelLeachim/file_miktau/blob/master/4readme/autotags2.png)

These tags behave almost like regular aggregations, or facets in an Elastic app. 

### FS mapping

At this moment the FS mapping works as follows:

* Open in folder (will symlink all files related to a given tag selection into a folder)
* Add files (will symlink new files into the chosen root folder)
* Load folder (will load given folder and use its subfolders as tags)
* Add/remove tags (will rebuild directory tree according to a new taxonomy)

#### State reconciliation (dangerous moments)

At current point, tags are stored as folders within the root folder.
That means, that every mutable operation on a family of tags will
change the directory tree.

This was implemented this way because it is better to hold the single
source of truth (which is a file system), than multiple sources that 
will have to be synchronized. 

On the other hand, touching directory tree can have disasterous effects 
on already made taxonomy. 

## App structure and technical moments

The in memory app state store is implemented in GO, and should run fairly 
smoothly on ~30 000 files. 

On the client side the app uses ReFrame as its framework and is implemented
in ClojureScript

## TODO

* Build CSV backend instead of FS one
* Make it easier to navigate within the folder
