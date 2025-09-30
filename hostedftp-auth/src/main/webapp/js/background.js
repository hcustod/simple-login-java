(() => {
  const canvas = document.getElementById('bg-canvas');
  if (!canvas || typeof THREE === 'undefined') return;

  // --- Renderer / Scene / Camera ---
  const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  const scene = new THREE.Scene();

  const camera = new THREE.PerspectiveCamera(60, 1, 0.1, 1000);
  camera.position.z = 105;

  // --- Particles ---
  const COUNT  = 1800;
  const SPREAD = 200;

  const geometry  = new THREE.BufferGeometry();
  const positions = new Float32Array(COUNT * 3);
  const colors    = new Float32Array(COUNT * 3);
  const velocities= new Float32Array(COUNT * 3);

  const cGreen = new THREE.Color(0x86efac); // light green
  const cAmber = new THREE.Color(0xf59e0b); // autumn yellow

  for (let i = 0; i < COUNT; i++) {
    const i3 = i * 3;

    // Random cube spread
    positions[i3 + 0] = (Math.random() - 0.5) * SPREAD * 2;
    positions[i3 + 1] = (Math.random() - 0.5) * SPREAD * 1.2;
    positions[i3 + 2] = (Math.random() - 0.5) * SPREAD * 2;

    // Slow, slightly forward drift
    velocities[i3 + 0] = (Math.random() - 0.5) * 0.02;
    velocities[i3 + 1] = (Math.random() - 0.5) * 0.02;
    velocities[i3 + 2] = -0.02 - Math.random() * 0.03;

    // Blend between green & amber with a little HSL jitter
    const t    = Math.random();
    const col  = cGreen.clone().lerp(cAmber, t);
    const hsl  = {}; col.getHSL(hsl);
    hsl.s = Math.min(1, Math.max(0, hsl.s + (Math.random() - 0.5) * 0.15));
    hsl.l = Math.min(1, Math.max(0, hsl.l + (Math.random() - 0.5) * 0.10));
    col.setHSL(hsl.h, hsl.s, hsl.l);

    colors[i3 + 0] = col.r;
    colors[i3 + 1] = col.g;
    colors[i3 + 2] = col.b;
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  geometry.setAttribute('color',     new THREE.BufferAttribute(colors, 3));

  const material = new THREE.PointsMaterial({
    size: 1.8,
    sizeAttenuation: true,
    transparent: true,
    opacity: 0.85,
    depthWrite: false,
    blending: THREE.AdditiveBlending,
    vertexColors: true
  });

  const points = new THREE.Points(geometry, material);
  scene.add(points);

  // --- Resize / Fit canvas ---
  function resize() {
    const w = canvas.clientWidth || window.innerWidth;
    const h = canvas.clientHeight || window.innerHeight;
    renderer.setSize(w, h, false);
    camera.aspect = w / Math.max(1, h);
    camera.updateProjectionMatrix();
  }

  // --- Animation loop ---
  function tick() {
    resize();

    const pos = geometry.attributes.position.array;
    const vel = velocities;

    for (let i = 0; i < COUNT; i++) {
      const i3 = i * 3;

      pos[i3 + 0] += vel[i3 + 0];
      pos[i3 + 1] += vel[i3 + 1];
      pos[i3 + 2] += vel[i3 + 2];

      // wrap-around bounds
      if (pos[i3 + 0] < -SPREAD) pos[i3 + 0] =  SPREAD;
      if (pos[i3 + 0] >  SPREAD) pos[i3 + 0] = -SPREAD;
      if (pos[i3 + 1] < -SPREAD) pos[i3 + 1] =  SPREAD;
      if (pos[i3 + 1] >  SPREAD) pos[i3 + 1] = -SPREAD;
      if (pos[i3 + 2] < -SPREAD) pos[i3 + 2] =  SPREAD;
      if (pos[i3 + 2] >  SPREAD) pos[i3 + 2] = -SPREAD;
    }
    geometry.attributes.position.needsUpdate = true;

    // Gentle camera drift + subtle “twinkle”
    const t = performance.now() * 0.00025;
    camera.position.x = Math.cos(t * 0.9) * 6;
    camera.position.y = Math.sin(t * 1.3) * 4;
    camera.lookAt(0, 0, 0);

    material.opacity = 0.80 + Math.sin(t * 2.3) * 0.05;

    renderer.render(scene, camera);
    requestAnimationFrame(tick);
  }

  // Kick off
  const fit = () => { canvas.width = canvas.clientWidth; canvas.height = canvas.clientHeight; };
  window.addEventListener('resize', fit);
  fit();
  tick();
})();
