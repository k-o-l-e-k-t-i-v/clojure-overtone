(do (use 'late-night.core)
    (use 'overtone.core))

(definst bd1
  [fscale 0]
  (let [fenv (env-gen (perc 0 0.01 1 0)
                      1 1000 40 1)
        s (sin-osc fenv)
        k (klang [[40 90 230]
                  [0.8 0.3 0.2]
                  [0 0 0]]
                 (lin-lin (lf-noise2 8) -1 1 0 fscale) 0)

        denv (env-gen (perc 0 0.005 1 0)
                      1 30 1 1)

        fenv2 (env-gen (perc 0 0.003 1 0)
                       1 200 140 1)
        s (bpf s fenv2 0.3)
        s (distort (* denv s))
        s (normalizer s)
        h (freq-shift (normalizer k)
                      (lin-lin (lf-noise2 40)-1 1 -300 200))
        aenv (env-gen (perc 0 0.42 1 0)
                      1 1 0 1)
        aenv2 (env-gen (perc 0 0.282 1 0)
                      1 1 0 1)
        o (mix [(* s aenv)
                (* h aenv2 (lf-noise2 4))])
;        o (g-verb o 80 1 :taillevel 0.3 :drylevel 1)
        sil (detect-silence o 0.001 0.1 FREE)]
    (out b1 o)))

(defn bb []
  (bd1 [:head g1] :fscale (choose [4 8 12]))
  (let [x (rand 100)
        t (/ (metro-tick m1) 1000)]
    (if (> x 90)
      (ctl mst :del (choose [(* 0.001 t)
                             (* 0.002 t)
                             (* 0.004 t)
                             ])))
;
    ;; (if (> x 30)
    ;;   (ctl c :ct 0.0029
    ;;        :cd 1
    ;;        :namp 0.5))
    ))

(metro-tick m1)
(def m1 (metronome (* 120 4)))
(def p1 (ref [1 0 0 1 0 0 1 0]))
(at-zero-beat m1 8 p1 #'bb)


(stop)
(def g1 (group))
(def g2 (group :after g1))
(def b1 (audio-bus))

(definst ln-master-tool [del 0 amp 1 lofr 1 inbus 0]
  "tool for compensation of diferent starting point of metro during live coding performances

del: 0 -> time of delay in s
amp: 1 -> overall amplitude
lofr: 1 -> coefficient of low pass filter aplied on changing values of previous arguments"
  (let [i (in:ar inbus)
        d1 (delay-n i 2 (lpf del lofr))
        a (* d1 (lpf amp lofr))]
    (out 0 (pan2 a 0 1))))
(def mst (ln-master-tool [:tail g2] :del 0 :amp 1 :lofr 1 :inbus b1))

(definst kk [amp 1 namp 0]
  (let [i (in:ar 0)
        v (g-verb i 100 3)
        o (mix [i (* (lpf namp 1) v)])
        o (normalizer o)]
    (replace-out 0 (* amp o))))

(def r (kk [:tail g2] :amp 1))
(kill kk )

(ctl r :namp 0.9)

(odoc detect-index)
(ctl mst :del 0.1)
(definst cb [amp 1 namp 0 ct 0.2 cd 0.5]
  (let [i (in:ar 0)
        v (comb-n i 2 ct cd)
        o (mix [i (* (lpf namp 1) v)])
        o (normalizer o)]
    (replace-out 0 (* amp o))))
(def c (cb [:tail g2] :amp 1 :namp 0 :ct 0.2 :cd 0.5))
(ctl c :namp 0.91 :ct 0.93 :cd 1)
(kill c)
(dosync (ref-set p1 [1 0 1 0 1 0 1 0]))
