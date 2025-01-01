import { NgModule } from '@angular/core';
import { CodeInputComponent } from './code-input.component';
import { CommonModule } from '@angular/common';
import { CodeInputComponentConfigToken } from './code-input.component.config';
import * as i0 from "@angular/core";
class CodeInputModule {
    static forRoot(config) {
        return {
            ngModule: CodeInputModule,
            providers: [
                { provide: CodeInputComponentConfigToken, useValue: config }
            ]
        };
    }
    /** @nocollapse */ static { this.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "16.0.2", ngImport: i0, type: CodeInputModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule }); }
    /** @nocollapse */ static { this.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "14.0.0", version: "16.0.2", ngImport: i0, type: CodeInputModule, declarations: [CodeInputComponent], imports: [CommonModule], exports: [CodeInputComponent] }); }
    /** @nocollapse */ static { this.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "16.0.2", ngImport: i0, type: CodeInputModule, imports: [CommonModule] }); }
}
export { CodeInputModule };
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "16.0.2", ngImport: i0, type: CodeInputModule, decorators: [{
            type: NgModule,
            args: [{
                    imports: [
                        CommonModule
                    ],
                    declarations: [
                        CodeInputComponent
                    ],
                    exports: [
                        CodeInputComponent
                    ]
                }]
        }] });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY29kZS1pbnB1dC5tb2R1bGUuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9hbmd1bGFyLWNvZGUtaW5wdXQvc3JjL2xpYi9jb2RlLWlucHV0Lm1vZHVsZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQXNCLFFBQVEsRUFBQyxNQUFNLGVBQWUsQ0FBQztBQUM1RCxPQUFPLEVBQUMsa0JBQWtCLEVBQUUsTUFBTSx3QkFBd0IsQ0FBQztBQUMzRCxPQUFPLEVBQUMsWUFBWSxFQUFDLE1BQU0saUJBQWlCLENBQUM7QUFDN0MsT0FBTyxFQUEyQiw2QkFBNkIsRUFBQyxNQUFNLCtCQUErQixDQUFDOztBQUV0RyxNQVdhLGVBQWU7SUFDMUIsTUFBTSxDQUFDLE9BQU8sQ0FBQyxNQUFnQztRQUM3QyxPQUFPO1lBQ0wsUUFBUSxFQUFFLGVBQWU7WUFDekIsU0FBUyxFQUFFO2dCQUNULEVBQUMsT0FBTyxFQUFFLDZCQUE2QixFQUFFLFFBQVEsRUFBRSxNQUFNLEVBQUU7YUFDNUQ7U0FDRixDQUFDO0lBQ0osQ0FBQztpSUFSVSxlQUFlO2tJQUFmLGVBQWUsaUJBTnhCLGtCQUFrQixhQUhsQixZQUFZLGFBTVosa0JBQWtCO2tJQUdULGVBQWUsWUFUeEIsWUFBWTs7U0FTSCxlQUFlOzJGQUFmLGVBQWU7a0JBWDNCLFFBQVE7bUJBQUM7b0JBQ1IsT0FBTyxFQUFFO3dCQUNQLFlBQVk7cUJBQ2I7b0JBQ0QsWUFBWSxFQUFFO3dCQUNaLGtCQUFrQjtxQkFDbkI7b0JBQ0QsT0FBTyxFQUFFO3dCQUNQLGtCQUFrQjtxQkFDbkI7aUJBQ0YiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQge01vZHVsZVdpdGhQcm92aWRlcnMsIE5nTW9kdWxlfSBmcm9tICdAYW5ndWxhci9jb3JlJztcbmltcG9ydCB7Q29kZUlucHV0Q29tcG9uZW50IH0gZnJvbSAnLi9jb2RlLWlucHV0LmNvbXBvbmVudCc7XG5pbXBvcnQge0NvbW1vbk1vZHVsZX0gZnJvbSAnQGFuZ3VsYXIvY29tbW9uJztcbmltcG9ydCB7Q29kZUlucHV0Q29tcG9uZW50Q29uZmlnLCBDb2RlSW5wdXRDb21wb25lbnRDb25maWdUb2tlbn0gZnJvbSAnLi9jb2RlLWlucHV0LmNvbXBvbmVudC5jb25maWcnO1xuXG5ATmdNb2R1bGUoe1xuICBpbXBvcnRzOiBbXG4gICAgQ29tbW9uTW9kdWxlXG4gIF0sXG4gIGRlY2xhcmF0aW9uczogW1xuICAgIENvZGVJbnB1dENvbXBvbmVudFxuICBdLFxuICBleHBvcnRzOiBbXG4gICAgQ29kZUlucHV0Q29tcG9uZW50XG4gIF1cbn0pXG5leHBvcnQgY2xhc3MgQ29kZUlucHV0TW9kdWxlIHtcbiAgc3RhdGljIGZvclJvb3QoY29uZmlnOiBDb2RlSW5wdXRDb21wb25lbnRDb25maWcpOiBNb2R1bGVXaXRoUHJvdmlkZXJzPENvZGVJbnB1dE1vZHVsZT4ge1xuICAgIHJldHVybiB7XG4gICAgICBuZ01vZHVsZTogQ29kZUlucHV0TW9kdWxlLFxuICAgICAgcHJvdmlkZXJzOiBbXG4gICAgICAgIHtwcm92aWRlOiBDb2RlSW5wdXRDb21wb25lbnRDb25maWdUb2tlbiwgdXNlVmFsdWU6IGNvbmZpZyB9XG4gICAgICBdXG4gICAgfTtcbiAgfVxufVxuIl19