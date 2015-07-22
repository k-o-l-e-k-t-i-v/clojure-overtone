(definst k
  []
  (let [t  (impulse:kr 4)
        fe (env-gen (perc 0 0.01 300 -50) t 1 80 1 )
        ae (env-gen (perc 0 0.3 1 0) t 1 0 1)
        s  (sin-osc fe)
        sa (* s ae)
        d1 (demand t 0 (drand [400 1200 40 5000 44100 34000 21000] INF))
        d2 (demand t 0 (drand [4 7 3 12 13 16 32 9] INF))
        sad (decimator sa 300 8)]
    (out 0 (pan2 sad 0 1))))

(k)

(def k1 (k))
(stop)
(odoc dwhite:dr)
(stop)
