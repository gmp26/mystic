(ns ^:figwheel-always mystic.main
    (:require [rum.core :as rum]
              [mystic.core :as core]
              [mystic.components :as comp]))

(defn el [id] (js/document.getElementById id))

(rum/mount (comp/side-by-side core/model) (el "main-app-area"))
