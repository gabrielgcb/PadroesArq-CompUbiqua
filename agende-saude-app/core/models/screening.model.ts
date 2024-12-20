import { ScreeningQuestionnaireAnswer } from "../vo/types/types";

export class Screening {

    constructor(
        public id: number = null,
        public questionnaire: ScreeningQuestionnaireAnswer[] = [],
        public notes: string = null,
        public classification: string = null,
        public justification: string = null,
        public status: string = null,
        public createdAt: Date = null,
        public updatedAt: Date = null
    ) { }

    public static from(notes: string, questionnaire: ScreeningQuestionnaireAnswer[]): Screening {
        return new Screening(null, questionnaire, notes);
    }

}