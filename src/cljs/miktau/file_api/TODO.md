# Okay, how do I handle files?

This name-space  initializes when on-change event 
happens. Here are possible triggers:

* on-drop-list-of-files/on-select-list-of-files
* on-choose-new-root/on-drop-new-root

How does handling look like? 
For example, on `on-choose-new-root` event:

call `api-handler` operation. Wait in the background while working.


Well, that was easy. No need for separate namespace.

Another one, is trickier. `on-drop-list-of-files`: 
* give them to the server
* on response: 
* if consistent, redirect to `edit-nodes` view
* if not, redirect to `resolve-conflict` view, where hold the following operations:
  * symlink conflicting files
  * copy conflicting files
  * move conflicting files
* Every operation (symlink/copy/move), looks like this:
  * call server
  * wait (show loading)
  * on result, if no error
  * redirect to `edit-nodes`
  
If I remove choice step, (or have it in settings, beforehand), then:
  * give them to server (server automatically handles conflicts according to settings)
  * on response redirect to edit nodes. (With selected ids of newly created items)
  * PROFIT
  
But, I need settings screen, what I would put there? 
  * Symlink/Copy/Move conflicting files
  * Choose root directory
  
