import { AfterViewChecked, Component, Input, OnInit, ViewChild } from "@angular/core";
import { ControlContainer } from "@angular/forms";
import { CONSTANTS } from "src/app/shared/campaigns.constants";
import { QuickSMSCampaignService } from "../quick-sms/service/quick-sms-campaigns-service";
import { CampaignsService } from "src/app/campaigns/campaigns.service";

@Component({
    selector: "app-campaign-name",
    templateUrl: "./campaign-name.component.html",
    styleUrls: ["./campaign-name.component.css"]
})
export class CampaignNameComponent implements OnInit {
    public campaignNameInfoTxt = CONSTANTS.INFO_TXT.campaignName;

    public uniqueNameError = CONSTANTS.ERROR_DISPLAY.uniqueCampaignName;

    public splCharsError = CONSTANTS.ERROR_DISPLAY.campaignNameSplChars;

    public allowedSplChars = CONSTANTS.allowed_special_characters;

    public minLengthError = CONSTANTS.ERROR_DISPLAY.campaignNameMinLength;

    public maxLengthError = CONSTANTS.ERROR_DISPLAY.campaignNameMaxLength;

    public minimumLength = CONSTANTS.minLengthCampaignName;

    public maximumLength = CONSTANTS.maxLengthCampaignName;

    public campaignNameFormGroup: any;

    public errorapi :boolean;

    public checkUniqueApiError = 'Something went wrong. Please try again to check unique name'

    public loading$ = this.quickSMSCampaignService.loadingcampaignNames$.asObservable();
    @Input() campType: any;
    prevCampName = "";
    spinner = false;

    constructor(
        private quickSMSCampaignService: QuickSMSCampaignService,
        public controlContainer: ControlContainer,
        public c_service: CampaignsService
    ) {}

    // @ViewChild(CampaignNameComponent, { static: false })
    // c_name: CampaignNameComponent;
    
    ngOnInit(): void {
        this.campaignNameFormGroup = this.controlContainer.control;
        const user = this.c_service.getUser();
         const prePopulateCampNam = user.autogen_cname_yn;
        
        if (prePopulateCampNam == 1) {
        this.autoGenerateAndChkUniqueness();
        }
    }
    autoGenerateAndChkUniqueness(){
        this.c_service.preSetCampaignName(this.campaignNameFormGroup,this.campType);
        this.apiToCheckUniqueness(this.name.value, true)
    }
    
    get name() {
        return this.campaignNameFormGroup.controls.name;
    }

    chkUniqueCampaignNameExists(event: any) {
        let cname = (event.target.value as string).trim();
        if(this.prevCampName == "" ||  this.prevCampName != cname.toLowerCase().trim()) {
            this.apiToCheckUniqueness(cname, false)
        }
    }
    apiToCheckUniqueness(cname: string, isAutoGenerated : boolean ){
        this.errorapi = true;
        this.spinner = true;
        this.name.setValue(cname);
        
        this.campaignNameFormGroup.controls.name.setErrors({
            apiRequestError: true });
        
        if((cname.length >0 &&
             (!this.name.errors?.required && !this.name.errors?.minLengthInvalid && !this.name.errors?.maxLengthInvalid && !this.name.errors?.pattern )) 
                || isAutoGenerated ){
            
            cname = cname.toLowerCase();
            this.quickSMSCampaignService
            .checkUniqueCampaignNames(cname)
            .subscribe(
                res => {
                    this.prevCampName = cname.trim();
                    delete this.campaignNameFormGroup.controls.name.errors['apiRequestError'];
                    this.campaignNameFormGroup.controls.name.updateValueAndValidity();
                    this.errorapi = false;
                    this.spinner = false;
                    if(res.statusCode > 299 || res.statusCode < 200){
                       

                    }else if( !res.isUnique){

                        this.campaignNameFormGroup.controls.name.setErrors({
                            campaignNameExists: true });
                        if(isAutoGenerated){
                            this.autoGenerateAndChkUniqueness();
                        }
                    }
                },
                (err) => {
                    this.errorapi =true;
                    this.spinner = false;
                    this.campaignNameFormGroup.controls.name.setErrors({
                        apiRequestError: true });
                    
                }

            );
    
        }
    }

    retry(){
        
        const focus = document.getElementById("name") as HTMLImageElement;
        focus.focus();
        focus.blur();
    }

    focus() {
        const focus = document.getElementById("name") as HTMLImageElement;
        focus.focus();
        // focus.scrollIntoView();
    }

    scroll() {
        const focus = document.getElementById("name") as HTMLImageElement;
    
       focus.focus();
       // window.scrollTo(0,0)
        //console.log('inside scroll',focus.scrollTo(0,0));
        
    }
   
}