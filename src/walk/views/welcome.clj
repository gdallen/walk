(ns walk.views.welcome
  (:require [walk.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-css include-js html5]]
        [hiccup.element :only [javascript-tag]]
;        [noir.fetch.remotes :only [defremote]]
))

(defpage "/welcome" []
               (include-css "/css/canvas.css")
         (common/layout
          [:div#header 
           [:p "Welcome to walk"]]
          [:div#main
            [:div#description 
              [:p "Test Map"]
            ]
            [:div#map]
            [:div#notes
              [:p "Next Up: click logic will be added to the map"]
            ]
          ]
         ))
