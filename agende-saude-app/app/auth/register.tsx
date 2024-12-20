import { AppName, FlatButton, Flex, H5, Layout, Paragraph, Slider, TextButton, TextInput } from "@/core/components";
import { useEffect, useRef } from "react";
import { ConfirmationModalProps, SliderRef } from "@/core/vo/types/components.props";
import { FieldError, useForm } from "react-hook-form";
import { displayErrorMessage, displayInfoMessage, displaySuccessMessage } from "@/core/utils/toast.utils";
import { getForm, saveForm, clearForm } from "@/core/utils/storage.utils";
import { useModal } from "@/core/contexts/modal.provider";
import { yupResolver } from "@hookform/resolvers/yup";
import { createPersonSchema } from "@/core/vo/consts/schemas";
import { acceptTrueOrElse } from "@/core/utils/functions";
import { Person } from "@/core/models/person.model";
import { createPerson, existsByTaxId } from "@/core/services/person.service";
import { useRouter } from "expo-router";
import { ConfirmationModal } from "@/core/components/molecules/modals/ConfirmationModal";

const Register: React.FC = () => {
    const sliderRef = useRef<SliderRef>(null);
    const router = useRouter();
    const { displayModal, closeModal } = useModal();

    const { control, handleSubmit, formState, getValues, reset } = useForm({
        resolver: yupResolver(createPersonSchema),
        defaultValues: {
            fullName: "",
            user: { email: "", phone: "", taxId: "", password: "" },
        },
    });

    const { errors } = formState;

    const goToLogin = () => {
        router.navigate("/auth/login");
    };

    const proceedToNextStep = (): void => {
        saveForm("register", getValues());
        sliderRef.current?.nextSlide();
    };

    const validateStep = (fieldError: FieldError): void => {
        acceptTrueOrElse(
            fieldError?.message?.length > 0,
            () => displayErrorMessage("Erro no formulário", "Preencha todos os campos corretamente!"),
            () => proceedToNextStep()
        );
    }

    const validateTaxIdStep = async (taxId: string, fieldError: FieldError): Promise<void> => {
        const bool: boolean = await existsByTaxId(taxId);
        if(bool) 
            validateStep(fieldError);
        else 
            displayInfoMessage(
                'CPF já cadastrado', 
                'Já existe uma pessoa em nossa base de dados com esse CPF cadastrado'
            );
    }

    const onClickAdvanceTaxIdStep = async (fieldError: FieldError): Promise<void> => {
        const taxId: string = getValues()?.user?.taxId;
        if(taxId && taxId?.length === 1) {
            validateTaxIdStep(taxId, fieldError)
        } else 
            displayInfoMessage(
                'CPF não preenchido',
                'O campo deve ser preenchido para prosseguir'
            );
    }

    const onSubmit = async (data: Person): Promise<void> => {
        await createPerson(data);
        clearForm("register");
        displaySuccessMessage("Formulário enviado", "Os dados foram enviados com sucesso!");
        router.navigate("/auth/login");
    };

    const displayPreviousFormFillAttemptConfirmation = (form: Person): void => {
        displayModal({
            modal: (props: ConfirmationModalProps) => <ConfirmationModal {...props} />,
            header: null,
            showCloseButton: false,
            modalProps: {
                title: "Recuperar formulário",
                message: "Você deseja carregar os dados previamente preenchidos?",
                onCancel: () => closeModal(),
                onConfirm: () => {
                    reset(form);
                    closeModal();
                },
            },
        });
    }

    useEffect(() => {
        const checkFormFillAttempt = async () => {
            const form = await getForm<Person>("register");
            if (form) {
                displayPreviousFormFillAttemptConfirmation(form);
            }
        };
        checkFormFillAttempt();
    }, []);

    return (
        <Layout>
            <Flex flex={1} justify="space-between" align="center" gap={32}>
                <Flex align="center" gap={32}>
                    <AppName />
                    <H5>Cadastro de novo usuário</H5>
                </Flex>
                <Slider ref={sliderRef} allowDotsNavigation={true}>
                    <Flex justify="space-between" align="space-between" gap={16}>
                        <TextInput
                            name="fullName"
                            label="Nome completo"
                            icon="account-check"
                            control={control}
                        />
                        <FlatButton
                            type="primary"
                            onPress={() => validateStep(errors?.fullName)}
                        >
                            Seguinte
                        </FlatButton>
                    </Flex>
                    <Flex align="center" gap={4}>
                        <TextInput
                            name="user.taxId"
                            label="CPF"
                            icon="file-document"
                            control={control}
                        />
                        <FlatButton
                            type="primary"
                            onPress={() => onClickAdvanceTaxIdStep(errors?.user?.taxId)}
                        >
                            Seguinte
                        </FlatButton>
                    </Flex>
                    <Flex align="center" gap={4}>
                        <TextInput
                            name="user.phone"
                            label="Telefone"
                            icon="phone"
                            control={control}
                        />
                        <FlatButton
                            type="primary"
                            onPress={() => validateStep(errors?.user?.phone)}
                        >
                            Seguinte
                        </FlatButton>
                    </Flex>
                    <Flex align="center" gap={4}>
                        <TextInput
                            name="user.email"
                            label="E-mail"
                            icon="email"
                            control={control}
                        />
                        <TextInput
                            name="user.password"
                            label="Senha"
                            icon="lock-check"
                            control={control}
                        />
                        <FlatButton type="primary" onPress={handleSubmit(onSubmit)}>
                            Confirmar
                        </FlatButton>
                    </Flex>
                </Slider>
                <Flex align="center">
                    <Paragraph>Já possui cadastro?</Paragraph>
                    <TextButton type="primary" onPress={goToLogin}>Acesse sua conta</TextButton>
                </Flex>
            </Flex>
        </Layout>
    );
};

export default Register;