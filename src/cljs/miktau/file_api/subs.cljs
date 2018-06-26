(ns miktau.file-api.subs
  (:require [re-frame.core :as refe]))

"File"
  * "Choose root folder Ctrl+N"
  * "Import files from another folder"
  * "Open"
"Operations"
  * "Previous page Ctrl+Z"
  * "---------------"
  * "Switch to nodes  Ctrl+E"
  * "Switch to cloud  Ctrl+B"
  * "---------------"
  * "Reload app (in case of a problem) <f5>"
"About"
  * "TOS"
* "Components"
(refe/reg-sub
 :file-api/main-menu
 )
