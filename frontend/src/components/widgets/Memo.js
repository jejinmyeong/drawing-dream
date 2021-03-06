import styled from "styled-components";
import { motion } from "framer-motion";
import MemoList from "./memo/MemoList";
import { useEffect, useState } from "react";

const Container = styled(motion.div)`
  box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 12px;
  background-color: ${({ theme }) => theme.ContainerColor};
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 10px;
  max-height: 40rem;
`;

const Wrapper = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 1rem 2rem;
  width: 85%;
  margin-bottom: 1rem;
`;

const Title = styled.h3`
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
  width: fit-content;
`;

const CloseButton = styled.div`
  display: flex;
  align-items: center;
`;

const Memo = ({
  isShow,
  setIsShow,
  isntShow,
  setIsntShow,
  widgetId,
  setWidgetId,
}) => {
  const close = () => {
    const newIsShow = isShow.filter((wid) => {
      return wid !== widgetId;
    });
    setIsShow(newIsShow);
    setIsntShow([...isntShow, widgetId]);
  };
  const [isListLoading, setIsListLoading] = useState(true);
  useEffect(() => {
    if (!isListLoading) {
      setIsListLoading(true);
    }
  }, [widgetId]);
  return (
    <Container
      layout
      layoutId="M04"
      whileHover={{
        scale: 1.01,
      }}
      onClick={() => {
        if (setWidgetId) setWidgetId("M04");
      }}
    >
      <Wrapper>
        <Title>메모</Title>
        {setIsShow && <CloseButton onClick={close}>❌</CloseButton>}
      </Wrapper>
      <MemoList
        main
        isListLoading={isListLoading}
        setIsListLoading={setIsListLoading}
      />
    </Container>
  );
};

export default Memo;
