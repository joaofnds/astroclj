(ns astro.env
  (:require
   [selmer.parser :as parser]
   [clojure.tools.logging :as log]
   [astro.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[astro started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[astro has shut down successfully]=-"))
   :middleware wrap-dev})
