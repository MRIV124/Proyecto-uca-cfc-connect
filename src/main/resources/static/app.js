const modules = [
  {id:'clientes',label:'Clientes',singular:'cliente',icon:'♙',desc:'Personas inscritas y contactos del centro.',fields:[['nombre','Nombre','text',true],['correo','Correo electrónico','email',true],['telefono','Teléfono','tel',false]]},
  {id:'cursos',label:'Cursos',singular:'curso',icon:'▤',desc:'Oferta de cursos y disponibilidad de cupos.',fields:[['nombre','Nombre del curso','text',true],['precio','Precio','number',true],['cupo','Cupo disponible','number',true]]},
  {id:'diplomados',label:'Diplomados',singular:'diplomado',icon:'▥',desc:'Programas de formación y su capacidad.',fields:[['nombre','Nombre del diplomado','text',true],['precio','Precio','number',true],['cupo','Cupo disponible','number',true]]},
  {id:'inscripciones',label:'Inscripciones',singular:'inscripción',icon:'☷',desc:'Registros de participación en cursos.',fields:[['clienteId','ID del cliente','number',true],['cursoId','ID del curso','number',true]]},
  {id:'cotizaciones',label:'Cotizaciones',singular:'cotización',icon:'▧',desc:'Propuestas de cursos, espacios y servicios.',fields:[['clienteId','ID del cliente','number',true],['tipo','Tipo','select',true,['CURSO','DIPLOMADO','ESPACIO','CATERING','COMBINADO']],['descripcion','Descripción','textarea',false],['total','Total','number',true]]},
  {id:'espacios',label:'Espacios',singular:'espacio',icon:'⌂',desc:'Salones y espacios para actividades.',fields:[['nombre','Nombre del espacio','text',true],['tipo','Tipo de espacio','text',true],['capacidad','Capacidad','number',true],['precio','Precio','number',true],['equipamiento','Equipamiento','text',false],['disponible','Disponible','checkbox',false]]},
  {id:'catering',label:'Catering',singular:'servicio de catering',icon:'♨',desc:'Servicios de alimentación disponibles.',fields:[['nombre','Nombre del servicio','text',true],['precioPorPersona','Precio por persona','number',true]]},
  {id:'pagos',label:'Pagos',singular:'pago',icon:'＄',desc:'Pagos y estado de confirmación.',fields:[['clienteId','ID del cliente','number',true],['tipoReferencia','Tipo de referencia','select',true,['INSCRIPCION','COTIZACION','ESPACIO','CATERING']],['referenciaId','ID de referencia','number',true],['monto','Monto','number',true],['metodo','Método de pago','select',true,['EFECTIVO','TARJETA','TRANSFERENCIA','DEPOSITO']]]},
  {id:'usuarios',label:'Usuarios',singular:'usuario',icon:'♙',desc:'Cuentas de usuario y asignación de roles.',fields:[['nombre','Nombre','text',true],['email','Correo electrónico','email',true],['password','Contraseña','password',true],['rolId','ID del rol','number',true],['estado','Cuenta activa','checkbox',false]]},
  {id:'roles',label:'Roles',singular:'rol',icon:'◈',desc:'Roles asignados a los usuarios del sistema.',fields:[['nombre','Nombre del rol','text',true]]}
];
const $ = s => document.querySelector(s);
const data = Object.fromEntries(modules.map(m => [m.id, []]));
let active = 'inicio', toastTimer;
const labels = {id:'ID',nombre:'Nombre',correo:'Correo',email:'Correo',telefono:'Teléfono',precio:'Precio',precioPorPersona:'Precio por persona',cupo:'Cupo',inscritos:'Inscritos',cupoDisponible:'Cupo disponible',clienteId:'Cliente ID',clienteNombre:'Cliente',cursoId:'Curso ID',cursoNombre:'Curso',tipo:'Tipo',descripcion:'Descripción',total:'Total',estado:'Estado',fecha:'Fecha',capacidad:'Capacidad',equipamiento:'Equipamiento',disponible:'Disponible',tipoReferencia:'Referencia',referenciaId:'Referencia ID',monto:'Monto',metodo:'Método',rol:'Rol'};
const icons = {clientes:'♙',cursos:'▤',diplomados:'▥',inscripciones:'☷',cotizaciones:'▧',espacios:'⌂',catering:'♨',pagos:'＄',usuarios:'♙',roles:'◈'};

/* ------------------------------------------------------------------
   Seguridad (Fase 3): token JWT
   El token se obtiene en POST /auth/login y se envia en cada peticion
   como 'Authorization: Bearer <token>'. Se guarda en localStorage para
   no tener que iniciar sesion en cada recarga.
   ------------------------------------------------------------------ */
const TOKEN_KEY = 'cfc.token';
const USER_KEY  = 'cfc.user';

const getToken = () => { try { return localStorage.getItem(TOKEN_KEY); } catch { return null; } };
const getUser  = () => { try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null'); } catch { return null; } };
const saveSession = (token, user) => {
  try { localStorage.setItem(TOKEN_KEY, token); localStorage.setItem(USER_KEY, JSON.stringify(user || {})); } catch {}
};
const clearSession = () => {
  try { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(USER_KEY); } catch {}
};

function showLogin(message) {
  $('#login-screen').hidden = false;
  document.body.classList.add('locked');
  const error = $('#login-error');
  if (error) error.textContent = message || '';
}
function hideLogin() {
  $('#login-screen').hidden = true;
  document.body.classList.remove('locked');
}
function paintProfile(user) {
  const name = $('#profile-name'), role = $('#profile-role'), mark = $('#profile-mark');
  if (!user) return;
  if (name) name.textContent = user.nombre || user.email || 'Usuario';
  if (role) role.textContent = user.rol ? `Rol ${user.rol}` : 'Panel local';
  if (mark) mark.textContent = (user.nombre || user.email || 'U').trim().charAt(0).toUpperCase();
}
async function loadProfile() {
  try {
    const user = await api('/auth/me');
    if (user) { saveSession(getToken(), user); paintProfile(user); }
  } catch {}
}
async function login(email, password) {
  const r = await fetch('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  let body = null;
  try { body = await r.json(); } catch {}
  if (!r.ok) throw new Error(body?.message || 'No se pudo iniciar sesion');
  saveSession(body.token, { email: body.email, nombre: body.nombre, rol: body.rol });
  return body;
}
function logout() {
  clearSession();
  // Se limpian los datos en memoria para no dejar registros a la vista.
  modules.forEach(m => { data[m.id] = []; });
  showLogin('Sesion cerrada. Vuelve a iniciar sesion.');
  toast('Sesion cerrada');
}

function money(v){return typeof v==='number'&&/precio|total|monto/i.test(String(this?.key||''))?new Intl.NumberFormat('es-SV',{style:'currency',currency:'USD'}).format(v):v}
function toast(message,error=false){const t=$('#toast');t.textContent=message;t.className=`toast show${error?' error':''}`;clearTimeout(toastTimer);toastTimer=setTimeout(()=>t.className='toast',3200)}
async function api(url,options={}){
  const headers={'Content-Type':'application/json',...(options.headers||{})};
  const token=getToken();
  if(token) headers['Authorization']=`Bearer ${token}`;
  const r=await fetch(url,{...options,headers});
  if(r.status===401){
    // Token ausente, invalido o expirado: se cierra la sesion y se vuelve al login.
    if(token){clearSession();showLogin('Tu sesion expiro. Vuelve a iniciar sesion.');}
    throw new Error('Sesion no autorizada. Inicia sesion para continuar.');
  }
  if(r.status===403){throw new Error('No tienes permisos para esta operacion (se requiere rol ADMIN).');}
  if(!r.ok){let message=`La operacion no se completo (${r.status}).`;try{const body=await r.json();message=body.message||body.mensaje||body.error||message;if(body.errors)message=Object.values(body.errors).join(' · ')}catch{}throw new Error(message)}
  if(r.status===204)return null;const text=await r.text();return text?JSON.parse(text):null}
function nav(){const host=$('#nav-modules');host.innerHTML='';modules.forEach(m=>{const b=document.createElement('button');b.className='nav-item'+(active===m.id?' active':'');b.innerHTML=`<span class="nav-icon">${icons[m.id]}</span>${m.label}`;b.onclick=()=>openModule(m.id);host.append(b)});document.querySelector('[data-page="inicio"]').classList.toggle('active',active==='inicio')}
function setPage(title,subtitle){$('#crumb-title').textContent=title;$('#page-title').textContent=title==='Resumen'?'Buenos dias, equipo 👋':title;$('#page-subtitle').textContent=subtitle}
function goHome(){active='inicio';nav();$('#overview').hidden=false;$('#resource-page').hidden=true;setPage('Resumen','Aqui tienes el estado general de la gestion academica.');refreshStats()}
async function refreshStats(){let total=0,connected=0;await Promise.all(modules.map(async m=>{try{const arr=await api(`/${m.id}`);data[m.id]=Array.isArray(arr)?arr:[];total+=data[m.id].length;connected++}catch{}}));$('#total-records').textContent=connected?total:'—';setApiStatus(connected>0);const cards=$('#module-cards');cards.innerHTML='';modules.forEach(m=>{const b=document.createElement('button');b.className='module-card';b.innerHTML=`<span class="mc-icon">${icons[m.id]}</span><b>${m.label}</b><small>${data[m.id].length} registros</small>`;b.onclick=()=>openModule(m.id);cards.append(b)})}
function setApiStatus(online){const text=document.querySelector('.api-state span');if(text)text.textContent=online?'API conectada':'API sin conexion';const stat=$('.service-status');if(stat){stat.textContent=online?'Operativo':'Sin conexion';stat.style.color=online?'#248354':'#b64b45'}}
async function openModule(id){active=id;nav();const m=modules.find(x=>x.id===id);$('#overview').hidden=true;$('#resource-page').hidden=false;$('#resource-overline').textContent='MODULO DE GESTION';$('#resource-heading').textContent=m.label;$('#resource-description').textContent=m.desc;$('#table-search').value='';$('#space-filter').hidden=id!=='espacios';$('#available-only').checked=false;$('#page-title').textContent=m.label;$('#page-subtitle').textContent=m.desc;$('#crumb-title').textContent=m.label;await loadModule()}
async function loadModule(){const m=modules.find(x=>x.id===active);if(!m)return;$('#table-body').innerHTML='<tr><td colspan="12" class="loading-cell">Cargando registros…</td></tr>';try{const suffix=active==='espacios'&&$('#available-only').checked?'?soloDisponibles=true':'';const result=await api(`/${active}${suffix}`);setApiStatus(true);data[active]=Array.isArray(result)?result:[];renderTable()}catch(e){setApiStatus(false);$('#table-body').innerHTML=`<tr><td colspan="12" class="loading-cell">${escapeHtml(e.message)}</td></tr>`;toast(e.message,true)}}
function escapeHtml(s){return String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
function display(key,value){if(value===null||value===undefined||value==='')return '—';if(typeof value==='boolean')return value?'Sí':'No';if(typeof value==='object')return value.nombre||value.name||value.id||JSON.stringify(value);if(typeof value==='number'&&/precio|total|monto/i.test(key))return new Intl.NumberFormat('es-SV',{style:'currency',currency:'USD'}).format(value);return value}
function renderTable(){const m=modules.find(x=>x.id===active);let rows=data[active]||[];if(active==='espacios'&&$('#available-only').checked)rows=rows.filter(r=>r.disponible);const q=$('#table-search').value.trim().toLocaleLowerCase('es');if(q)rows=rows.filter(r=>Object.values(r).some(v=>String(v??'').toLocaleLowerCase('es').includes(q)));$('#result-count').textContent=`${rows.length} ${rows.length===1?'registro':'registros'}`;$('#table-summary').textContent=`Mostrando ${rows.length} de ${(data[active]||[]).length} registros`;
 const visibleKeys=rows.length?Object.keys(rows[0]).filter(k=>!['password'].includes(k)).slice(0,7):['id',...m.fields.map(f=>f[0]).filter(k=>!['password'].includes(k))].slice(0,7);$('#table-head').innerHTML=`<tr>${visibleKeys.map(k=>`<th>${escapeHtml(labels[k]||k)}</th>`).join('')}<th class="actions-head">Acciones</th></tr>`;const body=$('#table-body');body.innerHTML='';rows.forEach(row=>{const tr=document.createElement('tr');tr.innerHTML=visibleKeys.map(k=>{let v=display(k,row[k]);if(['estado','disponible','cupoDisponible'].includes(k)){const good=(row[k]===true||['PAGADO','CONFIRMADA','APROBADA','ACTIVA'].includes(String(row[k])));const warn=['PENDIENTE'].includes(String(row[k]));return `<td><span class="state-pill ${good?'good':warn?'warn':'bad'}">${escapeHtml(v)}</span></td>`}return `<td class="${k==='id'?'id-cell':''}" title="${escapeHtml(v)}">${escapeHtml(v)}</td>`}).join('')+`<td><div class="row-actions">${specialButtons(row)}<button class="row-action edit" title="Editar" aria-label="Editar">✎</button><button class="row-action danger delete" title="Eliminar" aria-label="Eliminar">×</button></div></td>`;tr.querySelector('.edit').onclick=()=>showForm(row);tr.querySelector('.delete').onclick=()=>deleteRow(row);tr.querySelectorAll('[data-special]').forEach(b=>b.onclick=()=>runSpecial(row,b.dataset.special));body.append(tr)});$('#table-empty').hidden=rows.length>0;}
function specialButtons(row){const b=(action,label)=>`<button class="row-action special" data-special="${action}" title="${label}" aria-label="${label}">${label}</button>`;if(['cursos','diplomados'].includes(active))return b('inscribir','＋');if(active==='cotizaciones')return `${b('aprobar','✓')}${b('rechazar','↶')}`;if(active==='espacios')return b(row.disponible?'reservar':'liberar',row.disponible?'↗':'↙');if(active==='inscripciones')return b('estado','◷');if(active==='pagos'&&row.estado!=='PAGADO')return b('confirmar','✓');return ''}
async function runSpecial(row,action){try{let url=`/${active}/${row.id}/${action}`,options={method:'PATCH'};if(action==='estado'){const estado=prompt('Nuevo estado: PENDIENTE, CONFIRMADA, CANCELADA o FINALIZADA',row.estado||'PENDIENTE');if(!estado)return;url+=`?estado=${encodeURIComponent(estado.toUpperCase())}`}await api(url,options);toast('Accion completada');await loadModule()}catch(e){toast(e.message,true)}}
function showForm(row=null){const m=modules.find(x=>x.id===active);$('#dialog-title').textContent=row?`Editar ${m.singular}`:`Nuevo ${m.singular}`;$('#dialog-description').textContent=row?'Actualiza la informacion y guarda los cambios.':'Completa los campos para agregar un registro.';$('#form-error').textContent='';const host=$('#form-fields');host.innerHTML='';m.fields.forEach(([key,label,type,required,options])=>{const wrap=document.createElement('div');wrap.className='field'+(['textarea'].includes(type)?' wide':'');const l=document.createElement('label');l.htmlFor=`field-${key}`;l.textContent=label+(required?' *':'');wrap.append(l);let input;if(type==='select'){input=document.createElement('select');input.add(new Option('Selecciona una opcion',''));options.forEach(o=>input.add(new Option(o,o)))}else if(type==='textarea'){input=document.createElement('textarea');input.placeholder=label}else{input=document.createElement('input');input.type=type;if(type==='number'){input.step='any';input.min='0'}if(type==='checkbox')input.checked=row?Boolean(row[key]):key==='disponible'||key==='estado'}input.id=`field-${key}`;input.name=key;if(required&&type!=='checkbox')input.required=true;if(row&&type!=='checkbox'&&row[key]!==undefined)input.value=row[key];if(!row&&type==='select'&&options?.length)input.value='';wrap.append(input);host.append(wrap)});$('#record-form').dataset.id=row?.id||'';$('#submit-record').textContent=row?'Guardar cambios':'Crear registro';$('#record-dialog').showModal()}
function formPayload(){const m=modules.find(x=>x.id===active),out={};m.fields.forEach(([key,,type])=>{const el=document.getElementById(`field-${key}`);if(type==='checkbox')out[key]=el.checked;else if(type==='number')out[key]=el.value===''?null:Number(el.value);else out[key]=el.value.trim()||null});return out}
async function saveForm(e){e.preventDefault();const m=modules.find(x=>x.id===active),id=$('#record-form').dataset.id,payload=formPayload();const invalid=m.fields.find(([key,label,type,required])=>required&&(payload[key]===null||payload[key]===''||(type==='number'&&(!Number.isFinite(payload[key])||payload[key]<0))));if(invalid){$('#form-error').textContent=`Revisa el campo "${invalid[1]}".`;return}const btn=$('#submit-record');btn.disabled=true;try{await api(id?`/${active}/${id}`:`/${active}`,{method:id?'PUT':'POST',body:JSON.stringify(payload)});$('#record-dialog').close();toast(id?'Cambios guardados':'Registro creado');await loadModule();refreshStats()}catch(err){$('#form-error').textContent=err.message}finally{btn.disabled=false}}
async function deleteRow(row){if(!confirm(`¿Eliminar este registro de ${modules.find(x=>x.id===active).label.toLowerCase()}?`))return;try{await api(`/${active}/${row.id}`,{method:'DELETE'});toast('Registro eliminado');await loadModule();refreshStats()}catch(e){toast(e.message,true)}}
function updateDate(){const now=new Date();$('#today').textContent=new Intl.DateTimeFormat('es-SV',{weekday:'long',day:'numeric',month:'long',year:'numeric'}).format(now)}

/* --- arranque y eventos --- */
document.querySelector('[data-page="inicio"]').onclick=goHome;$('#new-record').onclick=()=>showForm();$('#record-form').addEventListener('submit',saveForm);$('#close-dialog').onclick=$('#cancel-dialog').onclick=()=>$('#record-dialog').close();$('#refresh').onclick=loadModule;$('#table-search').addEventListener('input',renderTable);$('#available-only').addEventListener('change',loadModule);$('#menu-toggle').onclick=()=>$('#sidebar').classList.toggle('open');$('#record-dialog').addEventListener('click',e=>{if(e.target===$('#record-dialog'))$('#record-dialog').close()});document.addEventListener('keydown',e=>{if((e.ctrlKey||e.metaKey)&&e.key.toLowerCase()==='k'){e.preventDefault();$('#table-search').focus()}if(e.key==='Escape')$('#sidebar').classList.remove('open')});

async function doLogin(e){
  if(e){e.preventDefault();e.stopPropagation();}
  const btn=$('#login-submit'), error=$('#login-error');
  if(btn.disabled) return;
  error.textContent=''; btn.disabled=true; btn.textContent='Verificando…';
  try{
    const user=await login($('#login-email').value.trim(),$('#login-password').value);
    paintProfile(user); hideLogin(); toast(`Bienvenido, ${user.nombre||user.email}`); goHome();
  }catch(err){ error.textContent=err.message; }
  finally{ btn.disabled=false; btn.textContent='Entrar al panel'; }
}
$('#login-form').addEventListener('submit',doLogin);
$('#login-submit').addEventListener('click',doLogin);
$('#logout').onclick=logout;

updateDate();nav();
if(getToken()){ hideLogin(); paintProfile(getUser()); loadProfile(); goHome(); }
else { showLogin(); }
