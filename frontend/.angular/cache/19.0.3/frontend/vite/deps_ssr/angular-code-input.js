import { createRequire } from 'module';const require = createRequire(import.meta.url);
import {
  CommonModule,
  NgForOf
} from "./chunk-UR24FW3N.js";
import {
  Component,
  EventEmitter,
  Inject,
  InjectionToken,
  Input,
  NgModule,
  Optional,
  Output,
  ViewChildren,
  setClassMetadata,
  ɵɵNgOnChangesFeature,
  ɵɵadvance,
  ɵɵattribute,
  ɵɵclassProp,
  ɵɵdefineComponent,
  ɵɵdefineInjector,
  ɵɵdefineNgModule,
  ɵɵdirectiveInject,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵgetCurrentView,
  ɵɵlistener,
  ɵɵloadQuery,
  ɵɵnextContext,
  ɵɵproperty,
  ɵɵqueryRefresh,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵtemplate,
  ɵɵviewQuery
} from "./chunk-4QFSETBS.js";
import "./chunk-UGSA5QJH.js";
import "./chunk-5TU6VZ7C.js";
import "./chunk-R4GQBPDK.js";
import {
  __async
} from "./chunk-YHCV7DAQ.js";

// ../../../node_modules/angular-code-input/fesm2022/angular-code-input.mjs
var _c0 = ["input"];
function CodeInputComponent_span_0_Template(rf, ctx) {
  if (rf & 1) {
    const _r1 = ɵɵgetCurrentView();
    ɵɵelementStart(0, "span")(1, "input", 2, 0);
    ɵɵlistener("click", function CodeInputComponent_span_0_Template_input_click_1_listener($event) {
      ɵɵrestoreView(_r1);
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onClick($event));
    })("paste", function CodeInputComponent_span_0_Template_input_paste_1_listener($event) {
      const i_r3 = ɵɵrestoreView(_r1).index;
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onPaste($event, i_r3));
    })("input", function CodeInputComponent_span_0_Template_input_input_1_listener($event) {
      const i_r3 = ɵɵrestoreView(_r1).index;
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onInput($event, i_r3));
    })("keydown", function CodeInputComponent_span_0_Template_input_keydown_1_listener($event) {
      const i_r3 = ɵɵrestoreView(_r1).index;
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onKeydown($event, i_r3));
    });
    ɵɵelementEnd()();
  }
  if (rf & 2) {
    const ctx_r1 = ɵɵnextContext();
    ɵɵclassProp("code-hidden", ctx_r1.isCodeHidden);
    ɵɵadvance();
    ɵɵproperty("type", ctx_r1.inputType)("disabled", ctx_r1.disabled);
    ɵɵattribute("inputmode", ctx_r1.inputMode)("autocapitalize", ctx_r1.autocapitalize);
  }
}
var CodeInputComponentConfigToken = new InjectionToken("CodeInputComponentConfig");
var defaultComponentConfig = {
  codeLength: 4,
  inputType: "tel",
  inputMode: "numeric",
  initialFocusField: void 0,
  isCharsCode: false,
  isCodeHidden: false,
  isPrevFocusableAfterClearing: true,
  isFocusingOnLastByClickIfFilled: false,
  code: void 0,
  disabled: false,
  autocapitalize: void 0
};
var InputState;
(function(InputState2) {
  InputState2[InputState2["ready"] = 0] = "ready";
  InputState2[InputState2["reset"] = 1] = "reset";
})(InputState || (InputState = {}));
var CodeInputComponent = class _CodeInputComponent {
  constructor(config) {
    this.isNonDigitsCode = false;
    this.codeChanged = new EventEmitter();
    this.codeCompleted = new EventEmitter();
    this.placeholders = [];
    this.inputs = [];
    this.inputsStates = [];
    this.state = {
      isFocusingAfterAppearingCompleted: false,
      isInitialFocusFieldEnabled: false
    };
    Object.assign(this, defaultComponentConfig);
    if (!config) {
      return;
    }
    for (const prop in config) {
      if (!config.hasOwnProperty(prop)) {
        continue;
      }
      if (!defaultComponentConfig.hasOwnProperty(prop)) {
        continue;
      }
      this[prop] = config[prop];
    }
  }
  /**
   * Life cycle
   */
  ngOnInit() {
    this.state.isInitialFocusFieldEnabled = !this.isEmpty(this.initialFocusField);
    this.onCodeLengthChanges();
  }
  ngAfterViewInit() {
    this.inputsListSubscription = this.inputsList.changes.subscribe(this.onInputsListChanges.bind(this));
    this.onInputsListChanges(this.inputsList);
  }
  ngAfterViewChecked() {
    this.focusOnInputAfterAppearing();
  }
  ngOnChanges(changes) {
    if (changes.code) {
      this.onInputCodeChanges();
    }
    if (changes.codeLength) {
      this.onCodeLengthChanges();
    }
  }
  ngOnDestroy() {
    if (this.inputsListSubscription) {
      this.inputsListSubscription.unsubscribe();
    }
  }
  /**
   * Methods
   */
  reset(isChangesEmitting = false) {
    this.onInputCodeChanges();
    if (this.state.isInitialFocusFieldEnabled) {
      this.focusOnField(this.initialFocusField);
    }
    if (isChangesEmitting) {
      this.emitChanges();
    }
  }
  focusOnField(index) {
    if (index >= this._codeLength) {
      throw new Error("The index of the focusing input box should be less than the codeLength.");
    }
    this.inputs[index].focus();
  }
  onClick(e) {
    if (!this.isFocusingOnLastByClickIfFilled) {
      return;
    }
    const target = e.target;
    const last = this.inputs[this._codeLength - 1];
    if (target === last) {
      return;
    }
    const isFilled = this.getCurrentFilledCode().length >= this._codeLength;
    if (!isFilled) {
      return;
    }
    setTimeout(() => last.focus());
  }
  onInput(e, i) {
    const target = e.target;
    const value = e.data || target.value;
    if (this.isEmpty(value)) {
      return;
    }
    if (!this.canInputValue(value)) {
      e.preventDefault();
      e.stopPropagation();
      this.setInputValue(target, null);
      this.setStateForInput(target, InputState.reset);
      return;
    }
    const values = value.toString().trim().split("");
    for (let j = 0; j < values.length; j++) {
      const index = j + i;
      if (index > this._codeLength - 1) {
        break;
      }
      this.setInputValue(this.inputs[index], values[j]);
    }
    this.emitChanges();
    const next = i + values.length;
    if (next > this._codeLength - 1) {
      target.blur();
      return;
    }
    this.inputs[next].focus();
  }
  onPaste(e, i) {
    e.preventDefault();
    e.stopPropagation();
    const data = e.clipboardData ? e.clipboardData.getData("text").trim() : void 0;
    if (this.isEmpty(data)) {
      return;
    }
    const values = data.split("");
    let valIndex = 0;
    for (let j = i; j < this.inputs.length; j++) {
      if (valIndex === values.length) {
        break;
      }
      const input = this.inputs[j];
      const val = values[valIndex];
      if (!this.canInputValue(val)) {
        this.setInputValue(input, null);
        this.setStateForInput(input, InputState.reset);
        return;
      }
      this.setInputValue(input, val.toString());
      valIndex++;
    }
    this.inputs[i].blur();
    this.emitChanges();
  }
  onKeydown(e, i) {
    return __async(this, null, function* () {
      const target = e.target;
      const isTargetEmpty = this.isEmpty(target.value);
      const prev = i - 1;
      const isBackspaceKey = yield this.isBackspaceKey(e);
      const isDeleteKey = this.isDeleteKey(e);
      if (!isBackspaceKey && !isDeleteKey) {
        return;
      }
      e.preventDefault();
      this.setInputValue(target, null);
      if (!isTargetEmpty) {
        this.emitChanges();
      }
      if (prev < 0 || isDeleteKey) {
        return;
      }
      if (isTargetEmpty || this.isPrevFocusableAfterClearing) {
        this.inputs[prev].focus();
      }
    });
  }
  onInputCodeChanges() {
    if (!this.inputs.length) {
      return;
    }
    if (this.isEmpty(this.code)) {
      this.inputs.forEach((input) => {
        this.setInputValue(input, null);
      });
      return;
    }
    const chars = this.code.toString().trim().split("");
    let isAllCharsAreAllowed = true;
    for (const char of chars) {
      if (!this.canInputValue(char)) {
        isAllCharsAreAllowed = false;
        break;
      }
    }
    this.inputs.forEach((input, index) => {
      const value = isAllCharsAreAllowed ? chars[index] : null;
      this.setInputValue(input, value);
    });
  }
  onCodeLengthChanges() {
    if (!this.codeLength) {
      return;
    }
    this._codeLength = this.codeLength;
    if (this._codeLength > this.placeholders.length) {
      const numbers = Array(this._codeLength - this.placeholders.length).fill(1);
      this.placeholders.splice(this.placeholders.length - 1, 0, ...numbers);
    } else if (this._codeLength < this.placeholders.length) {
      this.placeholders.splice(this._codeLength);
    }
  }
  onInputsListChanges(list) {
    if (list.length > this.inputs.length) {
      const inputsToAdd = list.filter((item, index) => index > this.inputs.length - 1);
      this.inputs.splice(this.inputs.length, 0, ...inputsToAdd.map((item) => item.nativeElement));
      const states = Array(inputsToAdd.length).fill(InputState.ready);
      this.inputsStates.splice(this.inputsStates.length, 0, ...states);
    } else if (list.length < this.inputs.length) {
      this.inputs.splice(list.length);
      this.inputsStates.splice(list.length);
    }
    this.onInputCodeChanges();
  }
  focusOnInputAfterAppearing() {
    if (!this.state.isInitialFocusFieldEnabled) {
      return;
    }
    if (this.state.isFocusingAfterAppearingCompleted) {
      return;
    }
    this.focusOnField(this.initialFocusField);
    this.state.isFocusingAfterAppearingCompleted = document.activeElement === this.inputs[this.initialFocusField];
  }
  emitChanges() {
    setTimeout(() => this.emitCode(), 50);
  }
  emitCode() {
    const code = this.getCurrentFilledCode();
    this.codeChanged.emit(code);
    if (code.length >= this._codeLength) {
      this.codeCompleted.emit(code);
    }
  }
  getCurrentFilledCode() {
    let code = "";
    for (const input of this.inputs) {
      if (!this.isEmpty(input.value)) {
        code += input.value;
      }
    }
    return code;
  }
  isBackspaceKey(e) {
    const isBackspace = e.key && e.key.toLowerCase() === "backspace" || e.keyCode && e.keyCode === 8;
    if (isBackspace) {
      return Promise.resolve(true);
    }
    if (!e.keyCode || e.keyCode !== 229) {
      return Promise.resolve(false);
    }
    return new Promise((resolve) => {
      setTimeout(() => {
        const input = e.target;
        const isReset = this.getStateForInput(input) === InputState.reset;
        if (isReset) {
          this.setStateForInput(input, InputState.ready);
        }
        resolve(input.selectionStart === 0 && !isReset);
      });
    });
  }
  isDeleteKey(e) {
    return e.key && e.key.toLowerCase() === "delete" || e.keyCode && e.keyCode === 46;
  }
  setInputValue(input, value) {
    const isEmpty = this.isEmpty(value);
    const valueClassCSS = "has-value";
    const emptyClassCSS = "empty";
    if (isEmpty) {
      input.value = "";
      input.classList.remove(valueClassCSS);
      input.parentElement.classList.add(emptyClassCSS);
    } else {
      input.value = value;
      input.classList.add(valueClassCSS);
      input.parentElement.classList.remove(emptyClassCSS);
    }
  }
  canInputValue(value) {
    if (this.isEmpty(value)) {
      return false;
    }
    const isDigitsValue = /^[0-9]+$/.test(value.toString());
    return isDigitsValue || this.isCharsCode || this.isNonDigitsCode;
  }
  setStateForInput(input, state) {
    const index = this.inputs.indexOf(input);
    if (index < 0) {
      return;
    }
    this.inputsStates[index] = state;
  }
  getStateForInput(input) {
    const index = this.inputs.indexOf(input);
    return this.inputsStates[index];
  }
  isEmpty(value) {
    return value === null || value === void 0 || !value.toString().length;
  }
  static {
    this.ɵfac = function CodeInputComponent_Factory(__ngFactoryType__) {
      return new (__ngFactoryType__ || _CodeInputComponent)(ɵɵdirectiveInject(CodeInputComponentConfigToken, 8));
    };
  }
  static {
    this.ɵcmp = ɵɵdefineComponent({
      type: _CodeInputComponent,
      selectors: [["code-input"]],
      viewQuery: function CodeInputComponent_Query(rf, ctx) {
        if (rf & 1) {
          ɵɵviewQuery(_c0, 5);
        }
        if (rf & 2) {
          let _t;
          ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.inputsList = _t);
        }
      },
      inputs: {
        codeLength: "codeLength",
        inputType: "inputType",
        inputMode: "inputMode",
        initialFocusField: "initialFocusField",
        isNonDigitsCode: "isNonDigitsCode",
        isCharsCode: "isCharsCode",
        isCodeHidden: "isCodeHidden",
        isPrevFocusableAfterClearing: "isPrevFocusableAfterClearing",
        isFocusingOnLastByClickIfFilled: "isFocusingOnLastByClickIfFilled",
        code: "code",
        disabled: "disabled",
        autocapitalize: "autocapitalize"
      },
      outputs: {
        codeChanged: "codeChanged",
        codeCompleted: "codeCompleted"
      },
      standalone: false,
      features: [ɵɵNgOnChangesFeature],
      decls: 1,
      vars: 1,
      consts: [["input", ""], [3, "code-hidden", 4, "ngFor", "ngForOf"], ["autocomplete", "one-time-code", 3, "click", "paste", "input", "keydown", "type", "disabled"]],
      template: function CodeInputComponent_Template(rf, ctx) {
        if (rf & 1) {
          ɵɵtemplate(0, CodeInputComponent_span_0_Template, 3, 6, "span", 1);
        }
        if (rf & 2) {
          ɵɵproperty("ngForOf", ctx.placeholders);
        }
      },
      dependencies: [NgForOf],
      styles: ["[_nghost-%COMP%]{--text-security-type: disc;--item-spacing: 4px;--item-height: 4.375em;--item-border: 1px solid #dddddd;--item-border-bottom: 1px solid #dddddd;--item-border-has-value: 1px solid #dddddd;--item-border-bottom-has-value: 1px solid #dddddd;--item-border-focused: 1px solid #dddddd;--item-border-bottom-focused: 1px solid #dddddd;--item-shadow-focused: 0px 1px 5px rgba(221, 221, 221, 1);--item-border-radius: 5px;--item-background: transparent;--item-font-weight: 300;--color: #171516;display:flex;transform:translateZ(0);font-size:inherit;color:var(--color)}[_nghost-%COMP%]   span[_ngcontent-%COMP%]{display:block;flex:1;padding-right:var(--item-spacing)}[_nghost-%COMP%]   span[_ngcontent-%COMP%]:first-child{padding-left:var(--item-spacing)}[_nghost-%COMP%]   span.code-hidden[_ngcontent-%COMP%]   input[_ngcontent-%COMP%]{text-security:var(--text-security-type);-webkit-text-security:var(--text-security-type);-moz-text-security:var(--text-security-type)}[_nghost-%COMP%]   input[_ngcontent-%COMP%]{width:100%;height:var(--item-height);color:inherit;background:var(--item-background);text-align:center;font-size:inherit;font-weight:var(--item-font-weight);border:var(--item-border);border-bottom:var(--item-border-bottom);border-radius:var(--item-border-radius);-webkit-appearance:none;transform:translateZ(0);-webkit-transform:translate3d(0,0,0);outline:none}[_nghost-%COMP%]   input.has-value[_ngcontent-%COMP%]{border:var(--item-border-has-value);border-bottom:var(--item-border-bottom-has-value)}[_nghost-%COMP%]   input[_ngcontent-%COMP%]:focus{border:var(--item-border-focused);border-bottom:var(--item-border-bottom-focused);box-shadow:var(--item-shadow-focused)}"]
    });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(CodeInputComponent, [{
    type: Component,
    args: [{
      selector: "code-input",
      template: '<span *ngFor="let holder of placeholders; index as i"\n      [class.code-hidden]="isCodeHidden">\n  <input #input\n         (click)="onClick($event)"\n         (paste)="onPaste($event, i)"\n         (input)="onInput($event, i)"\n         (keydown)="onKeydown($event, i)"\n         [type]="inputType"\n         [disabled]="disabled"\n         [attr.inputmode]="inputMode"\n         [attr.autocapitalize]="autocapitalize"\n         autocomplete="one-time-code"/>\n</span>\n',
      styles: [":host{--text-security-type: disc;--item-spacing: 4px;--item-height: 4.375em;--item-border: 1px solid #dddddd;--item-border-bottom: 1px solid #dddddd;--item-border-has-value: 1px solid #dddddd;--item-border-bottom-has-value: 1px solid #dddddd;--item-border-focused: 1px solid #dddddd;--item-border-bottom-focused: 1px solid #dddddd;--item-shadow-focused: 0px 1px 5px rgba(221, 221, 221, 1);--item-border-radius: 5px;--item-background: transparent;--item-font-weight: 300;--color: #171516;display:flex;transform:translateZ(0);font-size:inherit;color:var(--color)}:host span{display:block;flex:1;padding-right:var(--item-spacing)}:host span:first-child{padding-left:var(--item-spacing)}:host span.code-hidden input{text-security:var(--text-security-type);-webkit-text-security:var(--text-security-type);-moz-text-security:var(--text-security-type)}:host input{width:100%;height:var(--item-height);color:inherit;background:var(--item-background);text-align:center;font-size:inherit;font-weight:var(--item-font-weight);border:var(--item-border);border-bottom:var(--item-border-bottom);border-radius:var(--item-border-radius);-webkit-appearance:none;transform:translateZ(0);-webkit-transform:translate3d(0,0,0);outline:none}:host input.has-value{border:var(--item-border-has-value);border-bottom:var(--item-border-bottom-has-value)}:host input:focus{border:var(--item-border-focused);border-bottom:var(--item-border-bottom-focused);box-shadow:var(--item-shadow-focused)}\n"]
    }]
  }], function() {
    return [{
      type: void 0,
      decorators: [{
        type: Optional
      }, {
        type: Inject,
        args: [CodeInputComponentConfigToken]
      }]
    }];
  }, {
    inputsList: [{
      type: ViewChildren,
      args: ["input"]
    }],
    codeLength: [{
      type: Input
    }],
    inputType: [{
      type: Input
    }],
    inputMode: [{
      type: Input
    }],
    initialFocusField: [{
      type: Input
    }],
    isNonDigitsCode: [{
      type: Input
    }],
    isCharsCode: [{
      type: Input
    }],
    isCodeHidden: [{
      type: Input
    }],
    isPrevFocusableAfterClearing: [{
      type: Input
    }],
    isFocusingOnLastByClickIfFilled: [{
      type: Input
    }],
    code: [{
      type: Input
    }],
    disabled: [{
      type: Input
    }],
    autocapitalize: [{
      type: Input
    }],
    codeChanged: [{
      type: Output
    }],
    codeCompleted: [{
      type: Output
    }]
  });
})();
var CodeInputModule = class _CodeInputModule {
  static forRoot(config) {
    return {
      ngModule: _CodeInputModule,
      providers: [{
        provide: CodeInputComponentConfigToken,
        useValue: config
      }]
    };
  }
  static {
    this.ɵfac = function CodeInputModule_Factory(__ngFactoryType__) {
      return new (__ngFactoryType__ || _CodeInputModule)();
    };
  }
  static {
    this.ɵmod = ɵɵdefineNgModule({
      type: _CodeInputModule,
      declarations: [CodeInputComponent],
      imports: [CommonModule],
      exports: [CodeInputComponent]
    });
  }
  static {
    this.ɵinj = ɵɵdefineInjector({
      imports: [CommonModule]
    });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(CodeInputModule, [{
    type: NgModule,
    args: [{
      imports: [CommonModule],
      declarations: [CodeInputComponent],
      exports: [CodeInputComponent]
    }]
  }], null, null);
})();
export {
  CodeInputComponent,
  CodeInputComponentConfigToken,
  CodeInputModule,
  defaultComponentConfig
};
//# sourceMappingURL=angular-code-input.js.map
