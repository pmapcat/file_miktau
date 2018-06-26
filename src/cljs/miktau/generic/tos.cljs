(ns miktau.generic.tos
  (:require [miktau.app-metadata :refer [app-metadata]]))

(def default-eula-params
  {:company-name " Pumkin's CO "
   :apps " Metator's desktop applications, including Metator Desktop for Mac and Windows and associated documentation (the Software). "
   :email " info@pumkin.com "
   :address " Georgia, Batumi "
   :reasons
   [:div
    " three reasons"
    [:ul
     [:li "To receive and install updates;"]
     [:li "To send error reports"]
     [:li "to send anonymized usage information"]]
    "if you do not want the Software to update automatically or send error reports, or send anonymized usage information you must uninstall the Software. "]})

(defn eula []
  (let [params app-metadata
        company-name  (:company-name params)
        application-names  (:apps params)
        company-contact (:email params)
        company-place (:address params)
        communication-reasons (:reasons params)]
    [:div
     [:h1 "End-User License Agreement"]
     [:p
      "This End-User License Agreement (EULA) is a legal agreement between you (either as an individual or on behalf of an entity) and" company-name
      "regarding your use of"  application-names "IF YOU DO NOT AGREE TO ALL OF THE TERMS OF THIS EULA, DO NOT INSTALL, USE OR COPY THE SOFTWARE."]
     [:h2 "Summary"]
     [:ul
      [:li "You must agree to all of the terms of this EULA to use this Software."]
      [:li "If so, you may use the Software for any lawful purpose."]
      [:li "This Software automatically communicates with" company-name "servers for" communication-reasons]]
     [:p "This Software is provided \"as-is\" with no warranties, and you agree that" company-name "is not liable for anything you do with it."]

     [:h2 "The Agreement"]
     [:p "By downloading, installing, using, or copying the Software, you accept and agree to be bound by the terms of this EULA. If you do not agree to all of the terms of this EULA, you may not download, install, use or copy the Software."]

     [:h2  "The License"]
     "This EULA entitles you to install as many copies of the Software as you want, and use the Software for any lawful purpose consistent with this EULA. Your license to use the Software is expressly conditioned upon your agreement to all of the terms of this EULA."  company-name "reserves all other rights not granted by this EULA."

     [:h2 "The Restrictions"]
     [:ul
      [:li "When using the Software you must use it in a manner that complies with the applicable laws in the jurisdiction(s) in which you use the Software."]
      [:li "You may not sell, resell, rent, lease or exchange the Software for anything of value."]
      [:li "You may redistribute the software, but it must include this EULA and you may not repackage or bundle the Software with any other software."]
      [:li "You may not remove or alter any proprietary notices or marks on the Software."]]
     [:h2 "Privacy Notices"]
     [:p "The Software automatically communicates with" company-name "servers for"  communication-reasons]  

     [:p "Automatic Software Updates. The Software communicates with"  company-name  "servers  (and sends information described at the URL above) to determine whether there are any patches, bug fixes, updates, upgrades or other modifications to improve the Software. You agree that the Software may automatically install any such improvements to the Software on your computer without providing any further notice or receiving any additional consent. This feature may not be disabled. If you do not want to receive automatic updates, you must uninstall the Software."]

     [:p "Error Reports. In order to help us improve the Software, when the Software encounters certain errors, it will automatically send some information to"
      company-name "about the error (as described at the URL above). This feature may not be disabled. If you do not want to send error reports to" company-name "you must uninstall the Software."]

     [:h2 "Open-Source Notices"]
     [:p "Certain components of the Software may be subject to open-source software licenses (\"Open-Source Components\"), which means any software license approved as open-source licenses by the Open Source Initiative or any substantially similar licenses, including without limitation any license that, as a condition of distribution of the software licensed under such license, requires that the distributor make the software available in source code format. The Software documentation includes copies of the licenses applicable to the Open-Source Components."]

     [:p "To the extent there is conflict between the license terms covering the Open-Source Components and this EULA, the terms of such licenses will apply in lieu of the terms of this EULA. To the extent the terms of the licenses applicable to Open-Source Components prohibit any of the restrictions in this Agreement with respect to such Open-Source Component, such restrictions will not apply to such Open-Source Component. To the extent the terms of the licenses applicable to Open-Source Components require Licensor to make an offer to provide source code in connection with the Product, such offer is hereby made, and you may exercise it by contacting" company-contact] 

     [:h2 "Intellectual Property Notices"]
     [:p
      "The Software and all worldwide copyrights, trade secrets, and other intellectual property rights therein are the exclusive property of" company-name "."
      company-name "reserves all rights in and to the Software not expressly granted to you in this EULA."]

     [:h2 "Disclaimers and Limitations on Liability"]
     [:p "THE SOFTWARE IS PROVIDED ON AN \"AS IS\" BASIS, AND NO WARRANTY, EITHER EXPRESS OR IMPLIED, IS GIVEN. YOUR USE OF THE SOFTWARE IS AT YOUR SOLE RISK." company-name  "does not warrant that (i) the Software will meet your specific requirements; (ii) the Software is fully compatible with any particular platform; (iii) your use of the Software will be uninterrupted, timely, secure, or error-free; (iv) the results that may be obtained from the use of the Software will be accurate or reliable; (v) the quality of any products, services, information, or other material purchased or obtained by you through the Software will meet your expectations; or (vi) any errors in the Software will be corrected."]

     [:p "YOU EXPRESSLY UNDERSTAND AND AGREE THAT" company-name  "SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL OR EXEMPLARY DAMAGES, INCLUDING BUT NOT LIMITED TO, DAMAGES FOR LOSS OF PROFITS, GOODWILL, USE, DATA OR OTHER INTANGIBLE LOSSES" "(EVEN IF" company-name "HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES)" "RELATED TO THE SOFTWARE, including, for example: (i) the use or the inability to use the Software ; (ii) the cost of procurement of substitute goods and services resulting from any goods, data, information or services purchased or obtained or messages received or transactions entered into through or from the Software; (iii) unauthorized access to or alteration of your transmissions or data; (iv) statements or conduct of any third-party on the Software; (v) or any other matter relating to the Software."]
     [:p company-name "reserves the right at any time and from time to time to modify or discontinue, temporarily or permanently, the Software (or any part thereof) with or without notice."
      company-name "shall not be liable to you or to any third-party for any modification, price change, suspension or discontinuance of the Software."]

     [:h2 "Miscellanea"]
     [:p "The failure of" company-name "to exercise or enforce any right or provision of this EULA shall not constitute a waiver of such right or provision."
      "This EULA constitutes the entire agreement between you and" company-name  "and governs your use of the Software, superseding any prior agreements between you and"
      company-name "(including, but not limited to, any prior versions of the EULA)."]
     [:p "You agree that this EULA and your use of the Software are governed under " company-place  " law and any dispute related to the Software must be brought in a tribunal of competent jurisdiction located in or near" company-place
      "Please send any questions about this EULA to" company-contact]]))
